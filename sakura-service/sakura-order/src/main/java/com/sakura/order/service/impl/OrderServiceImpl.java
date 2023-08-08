package com.sakura.order.service.impl;

import com.sakura.common.api.ApiCode;
import com.sakura.common.exception.BusinessException;
import com.sakura.order.entity.Order;
import com.sakura.order.feign.ProductFeignService;
import com.sakura.order.feign.StockFeignService;
import com.sakura.order.mapper.OrderMapper;
import com.sakura.order.param.AddOrderParam;
import com.sakura.order.service.OrderService;
import com.sakura.order.param.OrderPageParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sakura.common.base.BaseServiceImpl;
import com.sakura.common.pagination.Paging;
import com.sakura.common.pagination.PageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.http.client.utils.DateUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 订单表 服务实现类
 *
 * @author Sakura
 * @since 2023-08-07
 */
@Slf4j
@Service
public class OrderServiceImpl extends BaseServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    StockFeignService stockFeignService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveOrder(AddOrderParam addOrderParam) throws Exception {
        // 先去查询商品库存信息
        Integer num = stockFeignService.getProductNum(addOrderParam.getProductNo());
        log.info("商品库存数量：" + num);
        if (num == null || num < 1 || num < addOrderParam.getNum()) {
            throw new BusinessException(500, "商品库存不足");
        }
        Order order = new Order();
        // 根据当前日期加随机数生成一个订单号
        order.setOrderNo(DateUtils.formatDate(new Date(), "yyyyMMddHHmmssSSS")
                + (int) ((Math.random() * 9 + 1) * 10000000));
        order.setProductNo(addOrderParam.getProductNo());
        order.setNum(addOrderParam.getNum());
        // 去商品服务获取商品单价
        Integer unitPrice = productFeignService.getUnitPrice(addOrderParam.getProductNo());
        log.info("商品单价：" + unitPrice);
        if (unitPrice == null || unitPrice < 0) {
            throw new BusinessException(500, "商品价格异常");
        }
        order.setTotalPrice(addOrderParam.getNum() * unitPrice);
        order.setStatus(1);
        orderMapper.insert(order);

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateOrder(Order order) throws Exception {
        return super.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteOrder(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public Paging<Order> getOrderPageList(OrderPageParam orderPageParam) throws Exception {
        Page<Order> page = new PageInfo<>(orderPageParam, OrderItem.desc(getLambdaColumn(Order::getCreateDt)));
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        IPage<Order> iPage = orderMapper.selectPage(page, wrapper);
        return new Paging<Order>(iPage);
    }

}
