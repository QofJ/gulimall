package com.atguigu.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

public class OrderConfirmVo {
    @Getter @Setter
    List<MemberAddressVo> address;
    @Getter @Setter
    List<OrderItemVo> items;
    @Getter @Setter
    Integer integration;
    @Getter @Setter
    String orderToken;
//    BigDecimal total;

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                sum.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
            }
        }
        return sum;
    }

//    BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
