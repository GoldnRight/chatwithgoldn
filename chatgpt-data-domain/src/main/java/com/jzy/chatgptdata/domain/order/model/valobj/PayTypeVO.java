package com.jzy.chatgptdata.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 支付类型
 * @create 2023-09-29 08:29
 */
@Getter
@AllArgsConstructor
public enum PayTypeVO {

    Ali_PAY(0, "支付宝支付"),
            ;

    private final Integer code;
    private final String desc;

    public static PayTypeVO get(Integer code){
        switch (code){
            case 0:
                return PayTypeVO.Ali_PAY;
            default:
                return PayTypeVO.Ali_PAY;
        }
    }

}
