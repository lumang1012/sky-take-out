package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.Interceptor;
import org.apache.el.parser.BooleanNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtProperties jwtProperties;

    //检验jwt

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(!(handler instanceof HandlerMethod)){
            //当前拦截的不是动态方法，放行
            return true;
        }
        //从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getUserTokenName());
        //校验令牌，并解析令牌获取用户id
        try{
            log.info("jwt校验{}",token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            BaseContext.setCurrentId(userId);//存储用户id
            return true;
        }catch(Exception ex){
            //不通过，响应401状态码
            response.setStatus(401);
            return false;
        }

    }
}
