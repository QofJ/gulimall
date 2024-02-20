package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            CartItem cartItem = new CartItem();
            //2、添加新商品到购物车
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(data.getSkuDefaultImg());
                cartItem.setTitle(data.getSkuTitle());
                cartItem.setSkuId(skuId);
                cartItem.setPrice(data.getPrice());
            }, executor);

            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);

            CompletableFuture.allOf(getSkuInfoTask, getSkuSaleAttrValues).join();

            String jsonString = JSON.toJSONString(cartItem);

            cartOps.put(skuId.toString(), jsonString);
            return cartItem;
        }
        else {
            //3、商品已经添加到购物车，修改数量即可
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(res, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
            if (tempCartItems != null) {
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                clearCartInfo(tempCartKey);
            }
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        } else {
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }


    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String json = (String) obj;
                CartItem cartItem = JSON.parseObject(json, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public void clearCartInfo(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String jsonString = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), jsonString);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String jsonString = JSON.toJSONString(cartItem);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(skuId.toString(), jsonString);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }
}
