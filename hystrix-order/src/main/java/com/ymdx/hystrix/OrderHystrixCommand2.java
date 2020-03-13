package com.ymdx.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.ymdx.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName: OrderHystrixCommand2
 * @Description: 采用信号量方式实现服务的隔离，熔断，降级
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-12 10:43
 * @Version: 1.0
 **/
public class OrderHystrixCommand2 extends HystrixCommand<JSONObject> {

    @Autowired
    private MemberService memberService;

    public OrderHystrixCommand2(MemberService memberService) {
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
        // 命令属性配置，采用信号量模式
        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                // 使用一个原子计数器（或信号量）来记录当前有多少个线程在运行，当请求进来时先判断计数器的数值，
                // 若超过设置的最大线程个数则拒绝该请求，若不超过则通行，这时候计数器+1，请求返回成功后计数器-1。
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(50);
        return HystrixCommand.Setter.withGroupKey(groupKey).andCommandPropertiesDefaults(commandProperties);
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
