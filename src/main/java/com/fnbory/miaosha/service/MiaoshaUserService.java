package com.fnbory.miaosha.service;

import com.fnbory.miaosha.dao.MiaoshaUserDao;
import com.fnbory.miaosha.domain.MiaoshaUser;
import com.fnbory.miaosha.exception.GlobalException;
import com.fnbory.miaosha.redis.MiaoshaUserkey;
import com.fnbory.miaosha.redis.RedisService;
import com.fnbory.miaosha.result.CodeMsg;
import com.fnbory.miaosha.util.MD5Util;
import com.fnbory.miaosha.util.UUIDUtil;
import com.fnbory.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: fnbory
 * @Date: 2019/6/15 14:30
 */
@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    private MiaoshaUserDao miaoshaUserDao;
    @Autowired
    private RedisService redisService;

    public MiaoshaUser getById(long id) {
        MiaoshaUser miaoshaUser=null;
        miaoshaUser=redisService.get(MiaoshaUserkey.getById,""+id,MiaoshaUser.class);
        if(miaoshaUser!=null){
            return miaoshaUser;
        }
        else {
            miaoshaUser=miaoshaUserDao.getById(id);
            if(miaoshaUser!=null){
                redisService.set(MiaoshaUserkey.getById,""+id,MiaoshaUser.class);
            }
            return miaoshaUser;
        }
    }
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserkey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserkey.token, token, user);
        return true;
    }

    public  MiaoshaUser getByToken(HttpServletResponse response,String token){
        if(token==null) {
            return null;
        }
        else{
            MiaoshaUser miaoshaUser=redisService.get(MiaoshaUserkey.token,token,MiaoshaUser.class);
            if(miaoshaUser!=null) {
                addCookie(response, token, miaoshaUser);
            }
            return miaoshaUser;
        }
    }

    public String login(HttpServletResponse response, LoginVo loginVo){
        if(loginVo==null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser miaoshaUser=getById(Long.valueOf(mobile));
        if(miaoshaUser==null) throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        //验证密码
        String dbPass = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        String token	 = UUIDUtil.uuid();
        addCookie(response, token, miaoshaUser);
        return token;

    }

    public  void addCookie(HttpServletResponse response, String token, MiaoshaUser miaoshaUser){
        // redis 缓存 key: token value：miaoshaueser
        redisService.set(MiaoshaUserkey.token,token,miaoshaUser);
        Cookie cookie=new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserkey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
