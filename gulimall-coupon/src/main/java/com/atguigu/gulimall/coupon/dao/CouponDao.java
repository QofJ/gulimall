package com.atguigu.gulimall.coupon.dao;

import com.atguigu.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author medizana
 * @email medizana@gmail.com
 * @date 2023-11-17 20:17:54
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
