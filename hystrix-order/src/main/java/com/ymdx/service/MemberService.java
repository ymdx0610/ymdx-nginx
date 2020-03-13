package com.ymdx.service;

import com.alibaba.fastjson.JSONObject;
import com.ymdx.utils.HttpClientUtils;
import org.springframework.stereotype.Service;

/**
 * @ClassName: MemberService
 * @Description: TODO
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-12 10:36
 * @Version: 1.0
 **/
@Service
public class MemberService {

    public JSONObject getMember() {
        JSONObject result = HttpClientUtils.httpGet("http://127.0.0.1:8081/member/memberIndex");
        return result;
    }

}
