package com.fnbory.miaosha.redis;

/**
 * @Author: fnbory
 * @Date: 2019/6/22 19:41
 */
public class AccessKey extends  BasePrefix {

    public AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public  static  AccessKey withWxpire(int expireSeconds) {
        return new AccessKey(expireSeconds,"access");
    }
}
