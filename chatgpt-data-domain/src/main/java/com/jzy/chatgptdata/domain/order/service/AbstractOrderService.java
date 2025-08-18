package com.jzy.chatgptdata.domain.order.service;

import com.jzy.chatgptdata.domain.order.model.entity.*;
import com.jzy.chatgptdata.domain.order.model.valobj.PayStatusVO;
import com.jzy.chatgptdata.domain.order.repository.IOrderRepository;
import com.alipay.api.AlipayApiException;
import com.jzy.chatgptdata.types.common.Constants;
import com.jzy.chatgptdata.types.exception.ChatGPTException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 通过抽象类来定义下单的标准
 *
 * @author 小傅哥
 */
@Slf4j
public abstract class AbstractOrderService implements IOrderService {

    @Resource
    protected IOrderRepository orderRepository;

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) {
        try {
            // 0. 基础信息
            String openid = shopCartEntity.getOpenid();
            Integer productId = shopCartEntity.getProductId();

            // 1. 查询有效的未支付订单，如果存在直接返回支付宝支付 Ali_Pay
            UnpaidOrderEntity unpaidOrderEntity = orderRepository.queryUnPayOrder(shopCartEntity);
            if (null != unpaidOrderEntity && PayStatusVO.WAIT.equals(unpaidOrderEntity.getPayStatus()) && null != unpaidOrderEntity.getPayUrl()) {
                log.info("创建订单-存在，已生成支付宝支付，返回 openid: {} orderId: {} payUrl: {}", openid, unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getPayUrl());
                return PayOrderEntity.builder()
                        .openid(openid)
                        .orderId(unpaidOrderEntity.getOrderId())
                        .payUrl(unpaidOrderEntity.getPayUrl())
                        .payStatus(unpaidOrderEntity.getPayStatus())
                        .build();
            } else if (null != unpaidOrderEntity && null == unpaidOrderEntity.getPayUrl()) {
                log.info("创建订单-存在，未生成支付宝支付，返回 openid: {} orderId: {}", openid, unpaidOrderEntity.getOrderId());
//                PayOrderEntity payOrderEntity = this.doPrepayOrder(openid, unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getProductName(), unpaidOrderEntity.getTotalAmount());
                PayOrderEntity payOrderEntity = this.doPrepayOrder(openid, productId, unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount());
                log.info("创建订单-完成，生成支付单。openid: {} orderId: {} payUrl: {}", openid, payOrderEntity.getOrderId(), payOrderEntity.getPayUrl());
                return payOrderEntity;
            }

            // 2. 商品查询
            ProductEntity productEntity = orderRepository.queryProduct(productId);
            if (!productEntity.isAvailable()) {
                throw new ChatGPTException(Constants.ResponseCode.ORDER_PRODUCT_ERR.getCode(), Constants.ResponseCode.ORDER_PRODUCT_ERR.getInfo());
            }

            // 3. 保存订单
            OrderEntity orderEntity = this.doSaveOrder(openid, productEntity);

            // 4. 创建支付
//            PayOrderEntity payOrderEntity = this.doPrepayOrder(openid, orderEntity.getOrderId(), productEntity.getProductName(), orderEntity.getTotalAmount());
            PayOrderEntity payOrderEntity  = this.doPrepayOrder(openid, productId, productEntity.getProductName(), orderEntity.getOrderId(), orderEntity.getTotalAmount());
            log.info("创建订单-完成，生成支付单。openid: {} orderId: {} payUrl: {}", openid, orderEntity.getOrderId(), payOrderEntity.getPayUrl());

            return payOrderEntity;
        } catch (Exception e) {
            log.error("创建订单，已生成支付宝支付，返回 openid: {} productId: {}", shopCartEntity.getOpenid(), shopCartEntity.getProductId());
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }
    }

    protected abstract OrderEntity doSaveOrder(String openid, ProductEntity productEntity);

    protected abstract PayOrderEntity doPrepayOrder(String openid, String orderId, String productName, BigDecimal amountTotal);

    protected abstract PayOrderEntity doPrepayOrder(String openid, Integer productId, String productName, String orderId, BigDecimal amountTotal) throws AlipayApiException;


}
