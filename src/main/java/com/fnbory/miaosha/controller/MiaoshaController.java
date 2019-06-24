package com.fnbory.miaosha.controller;

import com.fnbory.miaosha.access.AccessLimit;
import com.fnbory.miaosha.domain.MiaoshaOrder;
import com.fnbory.miaosha.domain.MiaoshaUser;
import com.fnbory.miaosha.domain.OrderInfo;
import com.fnbory.miaosha.rabbitmq.MQSender;
import com.fnbory.miaosha.rabbitmq.MiaoshaMessage;
import com.fnbory.miaosha.redis.GoodsKey;
import com.fnbory.miaosha.redis.MiaoshaKey;
import com.fnbory.miaosha.redis.OrderKey;
import com.fnbory.miaosha.redis.RedisService;
import com.fnbory.miaosha.result.CodeMsg;
import com.fnbory.miaosha.result.Result;
import com.fnbory.miaosha.service.GoodsService;
import com.fnbory.miaosha.service.MiaoshaService;
import com.fnbory.miaosha.service.MiaoshaUserService;
import com.fnbory.miaosha.service.OrderService;
import com.fnbory.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: fnbory
 * @Date: 2019/6/16 20:20
 */
@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender sender;

    private Map<Long,Boolean> localOverMap=new HashMap<>();

    /**
     *
     * 系统初始化
     * @return
     */

    public  void afterPropertiesSet(){
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null) {
            return;
        }
        for(GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

//    @RequestMapping(value="/reset", method= RequestMethod.GET)
//    @ResponseBody
//    public Result<Boolean> reset(Model model) {
//        List<GoodsVo> goodsList = goodsService.listGoodsVo();
//        for(GoodsVo goods : goodsList) {
//            goods.setStockCount(10);
//            redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), 10);
//            localOverMap.put(goods.getId(), false);
//        }
//        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
//        redisService.delete(MiaoshaKey.isGoodsOver);
//        miaoshaService.reset(goodsList);
//        return Result.success(true);
//    }

    @RequestMapping("/do_miaosha")
    @ResponseBody
    public Result<Integer> list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean over=localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        long stock=redisService.decr(GoodsKey.getMiaoshaGoodsStock,""+goodsId);
        if(stock<0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        //判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//
//        long stock = goods.getStockCount();
//        if(stock <= 0) {
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
        //判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMwssage(mm);
        return Result.success(0);
        //减库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//        return Result.success(orderInfo);
    }

    @RequestMapping("/result")
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
    public  Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
                                          @RequestParam("goodsId")long goodsId,
                                          @RequestParam(value="verifyCode", defaultValue="0")int verifyCode){

        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path  =miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCod(HttpServletResponse response, MiaoshaUser user,
                                              @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
