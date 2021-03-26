package com.yutao.limit.demo.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.concurrent.Executors.*;

/**
 * @description:
 * @version: 1.0
 * @author: yutao.guo@hand-china.com
 * @date 2021/3/26
 */

@Service
public class LimitService {

    /** 队列id和队列的映射关系，队列里面存储的是每一次通过时候的时间戳，这样可以使得程序里有多个限流队列 */
    private volatile static Map<String, List<Long>> MAP = new ConcurrentHashMap<>();

    public String CounterLimiter(){
        long timeWindow = 1000L;
        // 获取当前时间
        long nowTime = System.currentTimeMillis();
        // 根据队列id，取出对应的限流队列，若没有则创建
        List<Long> list = MAP.computeIfAbsent("listId", k -> new LinkedList<>());
        // 如果队列还没满，则允许通过，并添加当前时间戳到队列开始位置
        if (list.size() < 10) {
            list.add(0, nowTime);
            return "success";
        }

        // 队列已满（达到限制次数），则获取队列中最早添加的时间戳
        Long farTime = list.get(10 - 1);
        // 用当前时间戳 减去 最早添加的时间戳
        if (nowTime - farTime <= timeWindow) {
            // 若结果小于等于timeWindow，则说明在timeWindow内，通过的次数大于count
            // 不允许通过
            return "throttle";
        } else {
            // 若结果大于timeWindow，则说明在timeWindow内，通过的次数小于等于count
            // 允许通过，并删除最早添加的时间戳，将当前时间添加到队列开始位置
            list.remove(10 - 1);
            list.add(0, nowTime);
            return "success";
        }
    }

    //test
    public static void main(String[] args) {
        ExecutorService pool = newFixedThreadPool(10);
        // 模拟4000次请求
        IntStream.range(0, 20).forEach(e -> {
//            try {
//                // 模拟请求延迟
//                TimeUnit.MILLISECONDS.sleep(1);
//            } catch (InterruptedException e1) {
//                //
//            }
            // 多线程执行
            pool.execute(()->{
                LimitService limitService = new LimitService();
                String temp = limitService.CounterLimiter();
                System.out.println(temp);
            });
        });
    }
}
