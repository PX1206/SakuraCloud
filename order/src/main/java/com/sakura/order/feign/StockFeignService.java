package com.sakura.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description: stock服务接口
 * @author: Sakura
 * @date: 2023/7/20 14:38
 */
@FeignClient(name = "stock-service", path = "/stock")
public interface StockFeignService {

    @RequestMapping("/reduct")
    public String reduct();
}