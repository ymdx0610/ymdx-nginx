package com.ymdx.service;

import org.springframework.stereotype.Service;

/**
 * @ClassName: OrderService
 * @Description: TODO
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-13 09:25
 * @Version: 1.0
 **/
@Service
public class OrderService {

    public boolean addOrder() {
        System.out.println("....DB--正在操作订单表...");
        return true;
    }

}
