package com.jzy.chatgptdata.domain.order.service;

import com.jzy.chatgptdata.domain.order.model.aggregate.CreateOrderAggregate;
import com.jzy.chatgptdata.domain.order.model.entity.OrderEntity;
import com.jzy.chatgptdata.domain.order.model.entity.PayOrderEntity;
import com.jzy.chatgptdata.domain.order.model.entity.ProductEntity;
import com.jzy.chatgptdata.domain.order.model.valobj.OrderStatusVO;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.jzy.chatgptdata.domain.order.model.valobj.PayStatusVO;
import com.jzy.chatgptdata.domain.order.model.valobj.PayTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderService extends AbstractOrderService {

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;
    @Resource
    private AlipayClient alipayClient;

    @Override
    protected OrderEntity doSaveOrder(String openid, ProductEntity productEntity) {
        OrderEntity orderEntity = new OrderEntity();
        // 数据库有幂等拦截，如果有重复的订单ID会报错主键冲突。如果是公司里一般会有专门的雪花算法UUID服务
        orderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        orderEntity.setOrderTime(new Date());
        orderEntity.setOrderStatus(OrderStatusVO.CREATE);
        orderEntity.setTotalAmount(productEntity.getPrice());
        orderEntity.setPayTypeVO(PayTypeVO.Ali_PAY);
        // 聚合信息
        CreateOrderAggregate aggregate = CreateOrderAggregate.builder()
                .openid(openid)
                .product(productEntity)
                .order(orderEntity)
                .build();
        // 保存订单；订单和支付，是2个操作。
        // 一个是数据库操作，一个是HTTP操作。所以不能一个事务处理，只能先保存订单再操作创建支付单，如果失败则需要任务补偿
        orderRepository.saveOrder(aggregate);
        return orderEntity;
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String openid, String orderId, String productName, BigDecimal amountTotal) {
//        PrepayRequest request = new PrepayRequest();
//        Amount amount = new Amount();
//        amount.setTotal(amountTotal.multiply(new BigDecimal(100)).intValue());
//        request.setAmount(amount);
//        request.setAppid(appid);
//        request.setMchid(mchid);
//        request.setDescription(productName);
//        request.setNotifyUrl(notifyUrl);
//        request.setOutTradeNo(orderId);
//
//        // 创建微信支付单，如果你有多种支付方式，则可以根据支付类型的策略模式进行创建支付单
//        String codeUrl = "";
//        if (null != payService) {
//            PrepayResponse prepay = payService.prepay(request);
//            codeUrl = prepay.getCodeUrl();
//        } else {
//            codeUrl = "因你未配置支付渠道，所以暂时不能生成有效的支付URL。请配置支付渠道后，在application-dev.yml中配置支付渠道信息";
//        }
//
//        PayOrderEntity payOrderEntity = PayOrderEntity.builder()
//                .openid(openid)
//                .orderId(orderId)
//                .payUrl(codeUrl)
//                .payStatus(PayStatusVO.WAIT)
//                .build();
//
//        // 更新订单支付信息
//        orderRepository.updateOrderPayInfo(payOrderEntity);
        return new PayOrderEntity();
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String openid, Integer productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", totalAmount.toString());
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOpenid(openid);
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setPayUrl(form);
        payOrderEntity.setPayStatus(PayStatusVO.WAIT);

        // 更新订单支付信息
        orderRepository.updateOrderPayInfo(payOrderEntity);
        return payOrderEntity;
    }

    @Override
    public boolean changeOrderPaySuccess(String orderId, String transactionId, BigDecimal totalAmount, Date payTime) {
        return orderRepository.changeOrderPaySuccess(orderId, transactionId, totalAmount, payTime);
    }

    @Override
    public boolean changeOrderPaySuccess(String orderId) {
        return orderRepository.changeOrderPaySuccess(orderId, null, null, null);
    }

    @Override
    public CreateOrderAggregate queryOrder(String orderId) {
        return orderRepository.queryOrder(orderId);
    }

    @Override
    public void deliverGoods(String orderId) {
        orderRepository.deliverGoods(orderId);
    }

    @Override
    public List<String> queryReplenishmentOrder() {
        return orderRepository.queryReplenishmentOrder();
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderRepository.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderRepository.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderRepository.changeOrderClose(orderId);
    }

    @Override
    public List<ProductEntity> queryProductList() {
        return orderRepository.queryProductList();
    }
}
