package com.atguigu.gulimall.cart.vo;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    List<CartItem> items;
    private Integer countNum;
    private Integer countType;
    private BigDecimal totalAmount;
    private BigDecimal reduce = new BigDecimal("0.00");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        if (items != null && items.size() > 0) {
            countNum = items.stream().map(CartItem::getCount).reduce(0, Integer::sum);
        } else {
            countNum = 0;
        }
        return countNum;
    }

    public Integer getCountType() {
        if (items != null && items.size() > 0) {
            countType = items.size();
        } else {
            countType = 0;
        }
        return countType;
    }

    public BigDecimal getTotalAmount() {
        if (items != null && items.size() > 0) {
            totalAmount = items.stream().map(CartItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            totalAmount = new BigDecimal("0.00");
        }
        totalAmount = totalAmount.subtract(getReduce());
        return totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
