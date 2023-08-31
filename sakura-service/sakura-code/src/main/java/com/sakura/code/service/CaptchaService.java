package com.sakura.code.service;

import com.sakura.code.vo.PictureCodeVo;

/**
 * @author Sakura
 * @date 2023/8/14 14:25
 */
public interface CaptchaService {

    /**
     * @description: 获取图片验证码
     * @author: Sakura
     * @date: 2023/8/14 14:28
     */
    PictureCodeVo getPictureCode() throws Exception;

}
