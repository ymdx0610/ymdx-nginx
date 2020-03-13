package com.ymdx.controller;

import com.alibaba.fastjson.JSONObject;
import com.ymdx.hystrix.OrderHystrixCommand;
import com.ymdx.hystrix.OrderHystrixCommand2;
import com.ymdx.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: OrderController
 * @Description: TODO
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-12 10:35
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private MemberService memberService;

    @RequestMapping("/orderIndex")
    public Object orderIndex() {
        JSONObject member = memberService.getMember();
        System.out.println("当前线程名称：" + Thread.currentThread().getName() + "，订单服务调用会员服务：member：" + member);
        return member;
    }

    @RequestMapping("/orderIndexHystrix")
    public Object orderIndexHystrix() {
        return new OrderHystrixCommand(memberService).execute();
    }

    @RequestMapping("/orderIndexHystrix2")
    public Object orderIndexHystrix2() {
        return new OrderHystrixCommand2(memberService).execute();
    }

    @RequestMapping("/findOrderIndex")
    public Object findIndex() {
        System.out.println("当前线程名称：" + Thread.currentThread().getName() + "，findOrderIndex");
        return "findOrderIndex";
    }

}
