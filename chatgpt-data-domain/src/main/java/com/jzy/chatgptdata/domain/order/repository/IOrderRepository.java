package com.jzy.chatgptdata.domain.order.repository;

import com.jzy.chatgptdata.domain.order.model.aggregate.CreateOrderAggregate;
import com.jzy.chatgptdata.domain.order.model.entity.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单仓储服务 —— domain 领域层就像一个饭点的厨师，他需要的各种材料，米、面、粮、油、水，都不是它生产的，它只是知道要做啥，要用啥，用通过管道【接口】把这些东西传递进来
 *
 * @author 小傅哥
 */
public interface IOrderRepository {

    UnpaidOrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity);

    ProductEntity queryProduct(Integer productId);

    void saveOrder(CreateOrderAggregate aggregate);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime);

    CreateOrderAggregate queryOrder(String orderId);

    void deliverGoods(String orderId);

    List<String> queryReplenishmentOrder();

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    List<ProductEntity> queryProductList();
}
