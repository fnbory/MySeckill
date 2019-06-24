package com.fnbory.miaosha.access;

import com.fnbory.miaosha.domain.MiaoshaUser;

/**
 * @Author: fnbory
 * @Date: 2019/6/22 17:20
 */
public class UserContext {

    private  static  ThreadLocal<MiaoshaUser> userHolder=new ThreadLocal<>();

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }

    public static void setUser(MiaoshaUser user) {
        UserContext.userHolder.set(user);
    }
}
