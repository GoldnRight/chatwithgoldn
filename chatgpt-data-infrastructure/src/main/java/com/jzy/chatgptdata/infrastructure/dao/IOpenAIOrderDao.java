package com.jzy.chatgptdata.infrastructure.dao;

import com.jzy.chatgptdata.infrastructure.po.OpenAIOrderPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description 订单Dao
 */
@Mapper
public interface IOpenAIOrderDao {

    OpenAIOrderPO queryUnPayOrder(OpenAIOrderPO openAIOrderPOReq);

    void insert(OpenAIOrderPO order);

    void updateOrderPayInfo(OpenAIOrderPO openAIOrderPO);

    int changeOrderPaySuccess(OpenAIOrderPO openAIOrderPO);

    OpenAIOrderPO queryOrder(String orderId);

    int updateOrderStatusDeliverGoods(String orderId);

    List<String> queryReplenishmentOrder();

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

//    void insert(OpenAIOrderPO order);

//    OpenAIOrderPO queryUnPayOrder(OpenAIOrderPO order);

//    void updateOrderPayInfo(OpenAIOrderPO order);

//    void changeOrderPaySuccess(OpenAIOrderPO order);
}
