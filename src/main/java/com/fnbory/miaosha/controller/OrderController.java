package com.fnbory.miaosha.controller;

import com.fnbory.miaosha.domain.MiaoshaUser;
import com.fnbory.miaosha.domain.OrderInfo;
import com.fnbory.miaosha.redis.RedisService;
import com.fnbory.miaosha.result.CodeMsg;
import com.fnbory.miaosha.result.Result;
import com.fnbory.miaosha.service.GoodsService;
import com.fnbory.miaosha.service.MiaoshaUserService;
import com.fnbory.miaosha.service.OrderService;
import com.fnbory.miaosha.vo.GoodsVo;
import com.fnbory.miaosha.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: fnbory
 * @Date: 2019/6/19 18:30
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser user,
                                      @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
