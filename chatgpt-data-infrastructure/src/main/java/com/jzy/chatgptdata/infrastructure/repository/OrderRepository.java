package com.jzy.chatgptdata.infrastructure.repository;

import com.jzy.chatgptdata.domain.order.model.aggregate.CreateOrderAggregate;
import com.jzy.chatgptdata.domain.order.model.entity.OrderEntity;
import com.jzy.chatgptdata.domain.order.model.entity.PayOrderEntity;
import com.jzy.chatgptdata.domain.order.model.entity.ProductEntity;
import com.jzy.chatgptdata.domain.order.model.entity.ShopCartEntity;
import com.jzy.chatgptdata.domain.order.model.valobj.OrderStatusVO;
import com.jzy.chatgptdata.domain.order.repository.IOrderRepository;
import com.jzy.chatgptdata.infrastructure.dao.IOpenAIOrderDao;
import com.jzy.chatgptdata.infrastructure.po.OpenAIOrderPO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 订单仓储实现
 *
 * @author 小傅哥
 */
@Repository
public class OrderRepository implements IOrderRepository {

    @Resource
    private IOpenAIOrderDao orderDao;

    @Override
    public OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity) {
        // 1. 封装参数
        OpenAIOrderPO orderReq = new OpenAIOrderPO();
        orderReq.setUserId(shopCartEntity.getUserId());
        orderReq.setProductId(shopCartEntity.getProductId());
        // 2. 查询到订单
        OpenAIOrderPO order = orderDao.queryUnPayOrder(orderReq);
        if (null == order) return null;
        // 3. 返回结果
        return OrderEntity.builder()
                .productId(order.getProductId())
                .productName(order.getProductName())
                .orderId(order.getOrderId())
                .orderStatus(OrderStatusVO.valueOf(order.getStatus()))
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .payUrl(order.getPayUrl())
                .build();
    }

    @Override
    public ProductEntity queryProductByProductId(String productId) {
        // 实际场景中会从数据库查询
        return ProductEntity.builder()
                .productId(productId)
                .productName("测试商品")
                .productDesc("这是一个测试商品")
                .price(new BigDecimal("1.68"))
                .build();
    }

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        String userId = orderAggregate.getUserId();
        ProductEntity productEntity = orderAggregate.getProductEntity();
        OrderEntity orderEntity = orderAggregate.getOrderEntity();

        OpenAIOrderPO order = new OpenAIOrderPO();
        order.setUserId(userId);
        order.setProductId(productEntity.getProductId());
        order.setProductName(productEntity.getProductName());
        order.setOrderId(orderEntity.getOrderId());
        order.setOrderTime(orderEntity.getOrderTime());
        order.setTotalAmount(productEntity.getPrice());
        order.setStatus(orderEntity.getOrderStatus().getCode());

        orderDao.insert(order);
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        OpenAIOrderPO order = new OpenAIOrderPO();
        order.setUserId(payOrderEntity.getUserId());
        order.setOrderId(payOrderEntity.getOrderId());
        order.setPayUrl(payOrderEntity.getPayUrl());
        order.setStatus(payOrderEntity.getOrderStatus().getCode());
        orderDao.updateOrderPayInfo(order);
    }

    @Override
    public void changeOrderPaySuccess(String orderId) {
        OpenAIOrderPO order = new OpenAIOrderPO();
        order.setOrderId(orderId);
        order.setStatus(OrderStatusVO.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(order);
    }

}
