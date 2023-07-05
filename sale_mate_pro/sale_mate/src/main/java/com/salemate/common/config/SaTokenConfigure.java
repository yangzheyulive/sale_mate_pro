package com.salemate.common.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * [Sa-Token 权限认证] 配置类
 */
@Configuration
public class SaTokenConfigure {

    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
//                .excludePathPatterns("/user/verify")
//                .excludePathPatterns("/user/login")
//                .excludePathPatterns("/user/env")
//                .excludePathPatterns("/user/install")
//                .excludePathPatterns("/wx/call/**");
                // 指定 拦截路由 与 放行路由
                .addInclude("/**")
                .addExclude("/user/verify")
                .addExclude("/user/login")
                .addExclude("/user/env")
                .addExclude("/user/install")
                .addExclude("/wx/call/**")
                // 认证函数: 每次请求执行
                .setAuth(obj -> {
                    System.out.println("---------- 进入Sa-Token全局认证 -----------");
                    // 登录认证 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                    StpUtil.checkLogin();
                    // 更多拦截处理方式，请参考“路由拦截式鉴权”章节 */
                })

                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> {
                    System.out.println("---------- 进入Sa-Token异常处理 -----------");
                    return SaResult.error(e.getMessage());
                })

                // 前置函数：在每次认证函数之前执行（BeforeAuth 不受 includeList 与 excludeList 的限制，所有请求都会进入）
                .setBeforeAuth(r -> {
                    SaHolder.getResponse()

                            // ---------- 设置跨域响应头 ----------
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", "*")
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "*")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "*")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600");

                    // 如果是预检请求，则立即返回到前端
                    SaRouter.match(SaHttpMethod.OPTIONS)
                            .free(j -> System.out.println("--------OPTIONS预检请求，不做处理"))
                            .back();

                });
    }
//    /**
//     * 权限校验
//     * @param registry
//     */
//    @Override
//    protected void addInterceptors (InterceptorRegistry registry){
//        registry.addInterceptor(new SaInterceptor(handle -> {
//            System.out.println("---------- SaInterceptor 拦截器 ----------");
//            System.out.println(handle);
//            StpUtil.checkLogin();
//        })).addPathPatterns("/**")

//    }

}

