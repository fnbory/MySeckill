package com.fnbory.miaosha.rabbitmq;

import com.fnbory.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: fnbory
 * @Date: 2019/6/19 21:01
 */
@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    private Logger log= LoggerFactory.getLogger(MQSender.class);

    public  void  sendMiaoshaMwssage(MiaoshaMessage message){
        String msg=RedisService.beanToString(message);
        log.info("send message:"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }

    public  void send(Object message){
        String msg=RedisService.beanToString(message);
        log.info("sendmas"+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    public  void sendtopic(Object message){
        String msg=RedisService.beanToString(message);
        log.info("send_topic_mas"+msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY1,msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,MQConfig.ROUTING_KEY2,msg+"2");
    }

    public  void sendfanout(Object message){
        String msg=RedisService.beanToString(message);
        log.info("send_fanout_mas"+msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg+"1");
    }

    public  void sendheaders(Object message){
        String msg=RedisService.beanToString(message);
        log.info("send_fanout_mas"+msg);
        MessageProperties properties=new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj=new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }

}
