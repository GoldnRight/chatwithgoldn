package com.jzy.chatgptdata.trigger.job;

import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.jzy.chatgptdata.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description 超时关单任务
 */
@Slf4j
@Component()
public class TimeoutCloseOrderJob {

    @Resource
    private IOrderService orderService;

    @Resource
    AlipayClient alipayClient;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void exec() {
        try {
            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
            if (orderIds.isEmpty()) {
                log.info("定时任务，超时30分钟订单关闭，暂无超时未支付订单 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                // 先检查订单在支付宝是否存在
                if (!isOrderExistInAlipay(orderId)) {
                    log.warn("订单在支付宝中不存在，跳过关单处理，orderId: {}", orderId);
                    // 直接更新本地订单状态
                    orderService.changeOrderClose(orderId);
                    continue;
                }
                // 先关闭订单状态，保证业务幂等性
                boolean status = orderService.changeOrderClose(orderId);
                if (!status) {
                    log.warn("订单状态变更失败，orderId: {}", orderId);
                    continue;
                }
                // 支付宝关单
                AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
                AlipayTradeCloseModel model = new AlipayTradeCloseModel();
                model.setOutTradeNo(orderId);
                request.setBizModel(model);
                AlipayTradeCloseResponse response = alipayClient.execute(request);
                //调用成功，则处理业务逻辑
                if (!response.isSuccess()) {
                    log.error("支付宝关单失败：{}", response.getBody());
                    // 注意：此处不抛出异常，避免影响其他订单处理
                    // 可根据实际需求决定是否需要补偿机制或重试逻辑
                } else {
                    log.info("定时任务，超时30分钟订单关闭 orderId: {} status：{}", orderId, status);
                }
            }
        } catch (Exception e) {
            log.error("定时任务，超时30分钟订单关闭失败", e);
        }
    }

    // 添加订单查询逻辑
    private boolean isOrderExistInAlipay(String orderId) {
        try {
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            model.setOutTradeNo(orderId);
            queryRequest.setBizModel(model);
            AlipayTradeQueryResponse queryResponse = alipayClient.execute(queryRequest);
            // 如果订单存在，返回true；如果订单不存在，返回false
            return !"ACQ.TRADE_NOT_EXIST".equals(queryResponse.getSubCode());
        } catch (Exception e) {
            log.error("查询支付宝订单异常，orderId: {}", orderId, e);
            return false;
        }
    }

}
