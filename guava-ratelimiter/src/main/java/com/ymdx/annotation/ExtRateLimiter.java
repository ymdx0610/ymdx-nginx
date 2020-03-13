package com.ymdx.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: ExtRateLimiter
 * @Description: TODO
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-13 09:40
 * @Version: 1.0
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtRateLimiter {

    // 允许每秒向令牌桶中添加令牌的个数（固定的速率）
    double permitsPerSecond();

    // 尝试从令牌中获取令牌的超时时间，以毫秒为单位
    long timeout();

}
