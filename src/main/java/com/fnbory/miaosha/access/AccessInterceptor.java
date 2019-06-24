package com.fnbory.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.fnbory.miaosha.domain.MiaoshaUser;
import com.fnbory.miaosha.redis.AccessKey;
import com.fnbory.miaosha.redis.RedisService;
import com.fnbory.miaosha.result.CodeMsg;
import com.fnbory.miaosha.result.Result;
import com.fnbory.miaosha.service.MiaoshaUserService;
import com.fnbory.miaosha.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @Author: fnbory
 * @Date: 2019/6/22 17:35
 */

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    RedisService redisService;

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            MiaoshaUser user=getuser(request,response);
            UserContext.setUser(user);
            HandlerMethod hm=(HandlerMethod) handler;
            AccessLimit accessLimit=hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit==null){
                return true;
            }
            int seconds=accessLimit.seconds();
            int maxCount=accessLimit.maxCount();
            boolean needLogin=accessLimit.needLogin();
            String key=request.getRequestURI();
            if(needLogin){
                if(user==null){

                }
                key+="_" + user.getId();
            }
            AccessKey accessKey=AccessKey.withWxpire(seconds);
            Integer count=redisService.get(accessKey,key,Integer.class);
            if(count  == null) {
                redisService.set(accessKey, key, 1);
            }else if(count < maxCount) {
                redisService.incr(accessKey, key);
            }else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }
        }
        return true;
    }

    private MiaoshaUser getuser(HttpServletRequest request, HttpServletResponse response) {
        String paramToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        return miaoshaUserService.getByToken(response, token);

    }

    private void render(HttpServletResponse response, CodeMsg cm) throws  Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[]  cookies = request.getCookies();
        if(cookies == null || cookies.length <= 0){
            return null;
        }
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
