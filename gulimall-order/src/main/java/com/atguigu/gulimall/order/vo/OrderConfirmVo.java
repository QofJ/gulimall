package com.atguigu.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderConfirmVo {
    @Getter @Setter
    List<MemberAddressVo> address;
    @Getter @Setter
    List<OrderItemVo> items;
    @Getter @Setter
    Integer integration;
    @Getter @Setter
    Map<Long, Boolean> stocks;
    @Getter @Setter
    String orderToken;
//    BigDecimal total;

    public Integer getCount() {
        Integer count = 0;
//        if (items != null) {
//            for (OrderItemVo item : items) {
//                count += item.getCount();
//            }
//        }
        count = (items == null) ?
                0 :
                items.stream().map(OrderItemVo::getCount).reduce(0, Integer::sum);
        return count;
    }

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                sum = sum.add(item.getPrice().multiply(new BigDecimal(item.getCount())));
            }
        }
        return sum;
    }

//    BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }
}
