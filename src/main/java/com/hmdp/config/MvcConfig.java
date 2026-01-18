package com.hmdp.config;

import com.hmdp.utils.LoginInterceptor;
import com.hmdp.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 必须先添加刷新token的拦截器，再添加登录拦截器
        // 因为登录拦截器需要判断用户是否登录，而刷新token的拦截器会把用户信息保存到ThreadLocal中
        // 如果先添加登录拦截器，ThreadLocal中没有用户信息，就会导致登录拦截器拦截所有请求
        // 所以要保证刷新token的拦截器先执行

        // 登录拦截器，排除不需要登录的路径
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/shop/**",
                        "/voucher/**",
                        "/shop-type/**",
                        "/upload/**",
                        "/blog/hot",
                        "/user/code",
                        "/user/login"
                ).order(1);
        // token刷新的拦截器，拦截所有请求
        // order代表优先级，数字越小优先级越高，这里设置为0，保证先执行刷新token的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }
}
