package com.ymdx.web;

import com.google.common.util.concurrent.RateLimiter;
import com.ymdx.annotation.ExtRateLimiter;
import com.ymdx.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: IndexController
 * @Description: 模拟抢购
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-13 09:23
 * @Version: 1.0
 **/
@RestController
public class IndexController {

    @Autowired
    private OrderService orderService;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /**
     * 以每秒为单位固定的速率值 1r/s 每秒中往桶中存入一个令牌 <br/>
     * 相当于该接口每秒钟只能支持一个客户端访问 <br/>
     * 独立线程 <br/>
     */
    private RateLimiter rateLimiter = RateLimiter.create(1.0);

    @RequestMapping("/addOrder")
    public String addOrder(){
        // 1. 限流：尝试从令牌桶中获取令牌的超时等待时间为500ms。如果获取不到令牌，则走服务降级
        boolean b = rateLimiter.tryAcquire(500, TimeUnit.MILLISECONDS);
        if(!b){
            System.out.println("当前系统繁忙，请稍后重试！");
            return "当前系统繁忙，请稍后重试！";
        }
        // 2. 正常业务逻辑处理
        boolean b1 = orderService.addOrder();
        if(b1){
            return "恭喜您，抢购成功！";
        }
        return "很遗憾，抢购失败！";
    }

    // 以每秒添加1个令牌到令牌桶中，尝试从令牌桶中获取令牌的超时等待时间为500ms
    @ExtRateLimiter(permitsPerSecond = 1.0, timeout = 500)
    @RequestMapping("/addOrder2")
    public String addOrder2() {
        boolean b = orderService.addOrder();
        if(b){
            return "恭喜您，抢购成功！ " + format.format(System.currentTimeMillis());
        }
        return "很遗憾，抢购失败！ " + format.format(System.currentTimeMillis());
    }

}
