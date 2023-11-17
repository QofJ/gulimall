package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author medizana
 * @email medizana@gmail.com
 * @date 2023-11-17 17:04:00
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
