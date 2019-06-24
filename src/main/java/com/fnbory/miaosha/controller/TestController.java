package com.fnbory.miaosha.controller;

import com.fnbory.miaosha.rabbitmq.MQSender;
import com.fnbory.miaosha.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @Autowired
    MQSender mqSender;

    @RequestMapping("/demo")
    public  String demo(){
        return "hello";
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> send(){
        mqSender.send("hello");
        return Result.success("hello");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> sendtopic(){
        mqSender.sendtopic("hello");
        return Result.success("hello");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> sendfanout(){
        mqSender.sendfanout("hello");
        return Result.success("hello");
    }

    @RequestMapping("/mq/headers")
    @ResponseBody
    public Result<String> sendheaders(){
        mqSender.sendheaders("hello");
        return Result.success("hello");
    }
}
