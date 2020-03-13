package com.ymdx.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: MemberController
 * @Description: TODO
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-12 10:52
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/member")
public class MemberController {

    @RequestMapping("/memberIndex")
    public Object memberIndex() throws InterruptedException {
        Map<String, Object> hashMap = new HashMap<>(10);
        hashMap.put("code", 200);
        hashMap.put("msg", "memberIndex");
        Thread.sleep(1500);
        return hashMap;
    }

}
