package com.fnbory.miaosha.rabbitmq;

import com.fnbory.miaosha.domain.MiaoshaOrder;
import com.fnbory.miaosha.domain.MiaoshaUser;
import com.fnbory.miaosha.redis.RedisService;
import com.fnbory.miaosha.service.GoodsService;
import com.fnbory.miaosha.service.MiaoshaService;
import com.fnbory.miaosha.service.OrderService;
import com.fnbory.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: fnbory
 * @Date: 2019/6/19 21:03
 */
@Service
public class MQReceiver {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    MiaoshaService miaoshaService;

    private Logger log= LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.QUEUE)
    public  void receive(String message){
        log.info("receice msg"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public  void receivetopic1(String message){
        log.info("topic queue1 receice msg"+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public  void receivetopic2(String message){
        log.info("topic queue2 receice msg"+message);
    }

    @RabbitListener(queues = MQConfig.HEADERS_EXCHANGE)
    public  void receiveheaders(byte[] message){
        log.info("headers queue receice msg"+new String(message));
    }

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public  void receiveMiaosha(String message){
        MiaoshaMessage mm= RedisService.stringToBean(message,MiaoshaMessage.class);
        log.info("receive message:"+message);
        MiaoshaUser user = mm.getUser();
        long goodsId = mm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        miaoshaService.miaosha(user, goods);

    }
}
