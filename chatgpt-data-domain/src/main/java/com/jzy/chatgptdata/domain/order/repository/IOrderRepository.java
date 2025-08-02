package com.jzy.chatgptdata.domain.order.repository;

import com.jzy.chatgptdata.domain.order.model.aggregate.CreateOrderAggregate;
import com.jzy.chatgptdata.domain.order.model.entity.OrderEntity;
import com.jzy.chatgptdata.domain.order.model.entity.PayOrderEntity;
import com.jzy.chatgptdata.domain.order.model.entity.ProductEntity;
import com.jzy.chatgptdata.domain.order.model.entity.ShopCartEntity;

/**
 * 订单仓储服务 —— domain 领域层就像一个饭点的厨师，他需要的各种材料，米、面、粮、油、水，都不是它生产的，它只是知道要做啥，要用啥，用通过管道【接口】把这些东西传递进来
 *
 * @author 小傅哥
 */
public interface IOrderRepository {

    /**
     * 查询未支付订单
     *
     * @param shopCartEntity 购物车实体对象
     * @return 订单实体对象
     */
    OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity);

    /**
     * 模拟查询商品信息
     *
     * @param productId 商品ID
     * @return 商品实体对象
     */
    ProductEntity queryProductByProductId(String productId);

    /**
     * 保存订单对象
     *
     * @param orderAggregate 订单聚合
     */
    void doSaveOrder(CreateOrderAggregate orderAggregate);

    /**
     * 更新订单支付信息
     *
     * @param payOrderEntity 支付单
     */
    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    /**
     * 订单支付成功
     * @param orderId 订单ID
     */
    void changeOrderPaySuccess(String orderId);

}
