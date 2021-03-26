package com.yutao.limit.demo.web;

import com.yutao.limit.demo.service.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @version: 1.0
 * @author: yutao.guo@hand-china.com
 * @date 2021/3/26
 */
@RestController
@RequestMapping("/request/limit")
public class LimitController {

    @Autowired
    LimitService limitService;

    /**
     * 限流算法
     * @return 正常调用返回 success，限流情况下 返回 throttle
     */
    @RequestMapping("/test")
    public String CounterLimiter(){
        return limitService.CounterLimiter();
    }

}
