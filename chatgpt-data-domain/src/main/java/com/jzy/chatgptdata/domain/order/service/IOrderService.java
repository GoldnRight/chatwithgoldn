package com.jzy.chatgptdata.domain.order.service;

import com.jzy.chatgptdata.domain.order.model.entity.PayOrderEntity;
import com.jzy.chatgptdata.domain.order.model.entity.ShopCartEntity;

/**
 * 订单服务
 *
 * @author 小傅哥
 */
public interface IOrderService {

    /**
     * 通过购物车实体对象，创建支付单实体（用于支付）—— 所有的订单下单都从购物车开始触发
     *
     * @param shopCartEntity 购物车实体
     * @return 支付单实体
     */
    PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception;

    /**
     * 更新订单状态
     * @param orderId 订单ID
     */
    void changeOrderPaySuccess(String orderId);

}
