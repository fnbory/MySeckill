package com.fnbory.miaosha.controller;

import com.fnbory.miaosha.domain.MiaoshaUser;
import com.fnbory.miaosha.redis.RedisService;
import com.fnbory.miaosha.result.Result;
import com.fnbory.miaosha.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: fnbory
 * @Date: 2019/6/16 21:54
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user) {
        return Result.success(user);
    }

}
