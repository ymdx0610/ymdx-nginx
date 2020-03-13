package com.ymdx.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.ymdx.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: OrderHystrixCommand
 * @Description: 采用线程池方式实现服务的隔离，熔断，降级
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-12 10:39
 * @Version: 1.0
 **/
public class OrderHystrixCommand extends HystrixCommand<JSONObject> {

    @Autowired
    private MemberService memberService;

    public OrderHystrixCommand(MemberService memberService) {
        super(setter());
        this.memberService = memberService;
    }

    @Override
    protected JSONObject run() {
        JSONObject member = memberService.getMember();
        System.out.println("当前线程名称：" + Thread.currentThread().getName() + "，订单服务调用会员服务：member：" + member);
        return member;
    }

    private static Setter setter() {
        // 服务分组
        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("orders");
        // 服务标识
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("order");
        // 线程池名称
        HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("order-pool");

        // 线程池配置：线程池大小为10，线程存活时间15秒，队列等待的阈值为100，超过100执行拒绝策略
        HystrixThreadPoolProperties.Setter threadPoolProperties = HystrixThreadPoolProperties.Setter().withCoreSize(10)
                .withKeepAliveTimeMinutes(15).withQueueSizeRejectionThreshold(100);

        // 命令属性配置Hystrix，默认开启超时
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
                // 采用线程池方式实现服务隔离
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                // 禁止超时
                .withExecutionTimeoutEnabled(false);
        return HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
                .andThreadPoolPropertiesDefaults(threadPoolProperties).andCommandPropertiesDefaults(commandProperties);

    }

    @Override
    protected JSONObject getFallback() {
        // 如果Hystrix发生熔断，当前服务不可用，直接执行Fallback方法
        System.out.println("系统错误！");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 500);
        jsonObject.put("msg", "服务繁忙，请稍后重试！");
        return jsonObject;
    }

}
