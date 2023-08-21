package com.sakura.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sakura.common.constant.CommonConstant;
import com.sakura.common.exception.BusinessException;
import com.sakura.common.redis.RedisUtil;
import com.sakura.common.tool.SHA256Util;
import com.sakura.common.tool.StringUtil;
import com.sakura.user.entity.User;
import com.sakura.user.mapper.UserMapper;
import com.sakura.user.param.LoginParam;
import com.sakura.user.param.UserRegisterParam;
import com.sakura.user.service.UserService;
import com.sakura.user.param.UserPageParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sakura.common.base.BaseServiceImpl;
import com.sakura.common.pagination.Paging;
import com.sakura.common.pagination.PageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sakura.user.tool.CommonUtil;
import com.sakura.common.vo.UserInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * 用户表 服务实现类
 *
 * @author Sakura
 * @since 2023-08-14
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    CommonUtil commonUtil;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean register(UserRegisterParam userRegisterParam) throws Exception {
        // 获取真实手机号
        String mobile = commonUtil.getDecryptStr(userRegisterParam.getMobile(), userRegisterParam.getSaltKey(), false);
        if (!StringUtil.isValidPhoneNumber(mobile)) {
            throw new BusinessException(500, "手机号格式异常");
        }
        userRegisterParam.setMobile(mobile);

        // 先校验短信验证码是否正确
        if (!commonUtil.checkCode(CommonConstant.SMS_CODE + userRegisterParam.getMobile(), userRegisterParam.getSmsCode())) {
            throw new BusinessException(500, "短信验证码错误");
        }

        // 校验当前手机号是否已存在
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().
                eq(User::getMobile, userRegisterParam.getMobile()));
        if (user != null) {
            throw new BusinessException(500, "用户已存在");
        }

        // 添加用户信息
        user = new User();
        BeanUtils.copyProperties(userRegisterParam, user);
        user.setUserId(StringUtil.random(32)); // 生成用户ID
        user.setSalt(SHA256Util.getSHA256Str(UUID.randomUUID().toString())); // 随机生成盐
        // 获取用户真实密码
        String password = commonUtil.getDecryptStr(userRegisterParam.getPassword(), userRegisterParam.getSaltKey(), null);
        // HA256S加密
        user.setPassword(SHA256Util.getSHA256Str(password + user.getSalt()));
        user.setStatus(1);

        userMapper.insert(user);

        return true;
    }

    @Override
    public UserInfoVo login(LoginParam loginParam) throws Exception {
        // 获取真实手机号
        String mobile = commonUtil.getDecryptStr(loginParam.getMobile(), loginParam.getSaltKey(), false);
        // 获取用户真实密码
        String password = commonUtil.getDecryptStr(loginParam.getPassword(), loginParam.getSaltKey(), null);

        // 获取当前登录用户信息
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery()
                .eq(User::getMobile, mobile));
        if (user == null || !user.getPassword().equals(SHA256Util.getSHA256Str(password + user.getSalt()))) {
            throw new BusinessException(500, "用户名或密码错误"); // 此处写法固定，防止有人用脚本尝试账号
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(500, "账号状态异常，请联系管理员");
        }

        // 获取用户详细信息
        UserInfoVo userInfoVo = userMapper.findUserInfoVoById(user.getUserId());

        // 登录成功保存token信息
        String token = UUID.randomUUID().toString();
        userInfoVo.setToken(token);

        // 将信息放入Redis，有效时间2小时
        redisUtil.set(token, userInfoVo, 60*60*2);


        return userInfoVo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveUser(User user) throws Exception {
        return super.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUser(User user) throws Exception {
        return super.updateById(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteUser(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public Paging<User> getUserPageList(UserPageParam userPageParam) throws Exception {
        Page<User> page = new PageInfo<>(userPageParam, OrderItem.desc(getLambdaColumn(User::getCreateDt)));
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        IPage<User> iPage = userMapper.selectPage(page, wrapper);
        return new Paging<User>(iPage);
    }

}
