package com.salemate.common.handle;

import cn.dev33.satoken.exception.NotLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

//全局异常处理
@ControllerAdvice
public class WebExceptionHandle {
    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandle.class);

    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public String notLoginException(NotLoginException e, HttpServletResponse response){
        e.printStackTrace();
        response.setStatus(401);
        response.setContentType("application/text;charset=UTF-8");
        return "未登录";
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String exception(Exception e, HttpServletResponse response){
        response.setStatus(500);
        response.setContentType("application/text;charset=UTF-8");
        e.printStackTrace();
        return "服务器异常";
    }
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public String runtimeException(Exception e, HttpServletResponse response){
        e.printStackTrace();
        response.setStatus(500);
        response.setContentType("application/text;charset=UTF-8");
        return e.getMessage();
    }
}
