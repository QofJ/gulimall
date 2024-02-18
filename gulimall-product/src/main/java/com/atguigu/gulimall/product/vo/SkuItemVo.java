package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {
    SkuInfoEntity info;
    boolean hasStock = true;
    List<SkuImagesEntity> images;
    List<SkuItemSaleAttrVo> saleAttr;
    SpuInfoDescEntity desp;
    List<SpuItemAttrGroupVo> groupAttrs;
}
