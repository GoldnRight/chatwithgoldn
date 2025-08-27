package com.jzy.chatgptdata.infrastructure.dao;

import com.jzy.chatgptdata.infrastructure.po.OpenAIProductPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description 商品Dao
 */
@Mapper
public interface IOpenAIProductDao {

    OpenAIProductPO queryProductByProductId(Integer productId);

    List<OpenAIProductPO> queryProductList();

    String queryProductModelTypes(Integer productId);
}
