package com.ming.questionnaire.handler;

import com.alibaba.fastjson.JSON;

import com.ming.questionnaire.pojo.ResponseResult;
import com.ming.questionnaire.utils.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    // 自定义认证异常自定义实现类
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ResponseResult responseResult = new ResponseResult(HttpStatus.UNAUTHORIZED.value(),e.getMessage()); // 捕获抛出问题的信息
        String json = JSON.toJSONString(responseResult);  // 将返回值对象转化为JSON对象
        // 处理异常
        WebUtils.renderString(response,json);
    }
}
