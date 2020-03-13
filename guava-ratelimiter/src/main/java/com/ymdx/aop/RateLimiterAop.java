package com.ymdx.aop;

import com.google.common.util.concurrent.RateLimiter;
import com.ymdx.annotation.ExtRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RateLimiterAop
 * @Description: TODO
 * @Author: ymdx
 * @Email: y_m_d_x@163.com
 * @Date: 2020-03-13 09:44
 * @Version: 1.0
 **/
@Aspect
@Component
public class RateLimiterAop {

    private Map<String, RateLimiter> rateHashMap = new ConcurrentHashMap<>();

    /**
     * 定义切入点
     */
    @Pointcut("execution(public * com.ymdx.web.*.*(..))")
    private void pointCut(){}

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // 1. 判断请求方法上是否存在@ExtRateLimiter注解
        Method sinatureMethod = getSinatureMethod(proceedingJoinPoint);
        if (sinatureMethod == null) {
            return null;
        }

        // 2. 使用java反射机制获取拦截方法上自定义注解的参数
        ExtRateLimiter extRateLimiter = sinatureMethod.getDeclaredAnnotation(ExtRateLimiter.class);
        if (extRateLimiter == null) {
            // 直接进入实际请求方法中
            return proceedingJoinPoint.proceed();
        }
        double permitsPerSecond = extRateLimiter.permitsPerSecond();
        long timeout = extRateLimiter.timeout();

        // 3. 调用原生的RateLimiter创建令牌，保证每个请求对应都是单例的RateLimiter
        // 使用hashMap：key为请求的url地址，保证相同的请求在同一个桶
        String requestURI = getRequestURI();
        RateLimiter rateLimiter = null;
        if (rateHashMap.containsKey(requestURI)) {
            // 如果根据URL在hashMap能检测到RateLimiter
            rateLimiter = rateHashMap.get(requestURI);
        } else {
            // 添加新的RateLimiter到hashMap中
            rateLimiter = RateLimiter.create(permitsPerSecond);
            rateHashMap.put(requestURI, rateLimiter);
        }

        // 4. 获取令牌桶中的令牌，如果超时时间内没有获取到令牌，则直接调用本地服务降级方法，不会继续执行实际业务处理方法
        boolean tryAcquire = rateLimiter.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        if (!tryAcquire) {
            // 服务降级
            fallback();
            return null;
        }

        // 5. 执行实际业务处理方法
        return proceedingJoinPoint.proceed();
    }

    private void fallback() throws IOException {
        System.out.println("当前系统繁忙，请稍后重试！");
        // 在AOP编程中获取响应
        HttpServletResponse response = getResponse();
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        try {
            writer.println("当前系统繁忙，请稍后重试！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    private HttpServletResponse getResponse(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getResponse();
    }

    private String getRequestURI() {
        return getRequest().getRequestURI();
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    /**
     * 获取到AOP拦截的方法
     * @param proceedingJoinPoint
     * @return
     */
    private Method getSinatureMethod(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        return signature.getMethod();
    }


}
