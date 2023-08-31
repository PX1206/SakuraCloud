package com.sakura.code.service;

import com.sakura.code.param.SmsCodeParam;

/**
 * @author Sakura
 * @date 2023/8/14 14:25
 */
public interface SmsService {

    /**
     * @description: 获取短信验证码
     * @author: Sakura
     * @date: 2023/8/14 15:13
     */
    String getSMSCode(SmsCodeParam smsCodeParam) throws Exception;


}
