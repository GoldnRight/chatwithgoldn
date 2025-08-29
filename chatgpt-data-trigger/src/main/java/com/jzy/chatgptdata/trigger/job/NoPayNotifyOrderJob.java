package com.jzy.chatgptdata.trigger.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.jzy.chatgptdata.domain.order.producer.IOrderProducer;
import com.jzy.chatgptdata.domain.order.service.IOrderService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description 检测未接收到或未正确处理的支付回调通知
 */
@Slf4j
@Component()
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;

    @Resource
    AlipayClient alipayClient;

    @Resource
    private IOrderProducer orderProducer;

    @Resource
    private ThreadPoolExecutor orderDeliveryThreadPoolExecutor;

//    @Resource
//    private EventBus eventBus;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Timed(value = "no_pay_notify_order_job", description = "定时任务，订单支付状态更新")
    @Scheduled(cron = "0 0/1 * * * ?")
    public void exec() {
        try {
            if (null == alipayClient) {
                log.info("定时任务，订单支付状态更新。应用未配置支付渠道，任务不执行。");
                return;
            }
            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (orderIds.isEmpty()) {
                log.info("定时任务，订单支付状态更新，暂无未更新订单 orderId is null");
                return;
            }
            for (String orderId : orderIds) {
                // 查询结果
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                AlipayTradeQueryModel model = new AlipayTradeQueryModel();
                // 设置支付宝交易号
                model.setOutTradeNo(orderId);
                request.setBizModel(model);
                AlipayTradeQueryResponse response = alipayClient.execute(request);
                response.getTradeStatus();
                if (!"TRADE_SUCCESS".equals(response.getTradeStatus())) {
                    log.info("定时任务，订单支付状态更新，当前订单未支付 orderId is {}", orderId);
                    continue;
                }
                // 支付单号
                String transactionId = response.getOutTradeNo();
                Integer total = Integer.valueOf(response.getTotalAmount() == null ? "0" : response.getTotalAmount());
                BigDecimal totalAmount = new BigDecimal(total).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                String successTime = String.valueOf(response.getSendPayDate());
                // 更新订单
                boolean isSuccess = orderService.changeOrderPaySuccess(orderId, transactionId, totalAmount, dateFormat.parse(successTime));
                if (isSuccess) {
                    // 发布消息
                    orderDeliveryThreadPoolExecutor.execute(() -> {
                        orderProducer.sendPaymentSuccessMessage(orderId);
                    });
                }
            }
        } catch (Exception e) {
            log.error("定时任务，订单支付状态更新失败", e);
        }
    }

}
