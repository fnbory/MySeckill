package com.fnbory.miaosha.vo;

import com.fnbory.miaosha.domain.OrderInfo;

/**
 * @Author: fnbory
 * @Date: 2019/6/19 18:34
 */
public class OrderDetailVo {
    private GoodsVo goods;
    private OrderInfo order;
    public GoodsVo getGoods() {
        return goods;
    }
    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
    public OrderInfo getOrder() {
        return order;
    }
    public void setOrder(OrderInfo order) {
        this.order = order;
    }
}
