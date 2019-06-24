package com.fnbory.miaosha.redis;

/**
 * @Author: fnbory
 * @Date: 2019/6/15 15:11
 */
public class MiaoshaUserkey extends  BasePrefix {

    public  static final  int TOKEN_EXPIRE=3600*24*2;

    private  MiaoshaUserkey(int expireSeconds, String prefix){ super(expireSeconds,prefix);}

    public  static  MiaoshaUserkey token=new MiaoshaUserkey(TOKEN_EXPIRE,"tk");

    public  static MiaoshaUserkey getById=new MiaoshaUserkey(0,"id");

}
