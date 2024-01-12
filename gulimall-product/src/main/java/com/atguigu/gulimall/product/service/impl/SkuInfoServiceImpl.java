package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.hasLength((String) params.get("key")) && !"0".equalsIgnoreCase((String) params.get("key")), obj -> {
            obj.eq(SkuInfoEntity::getSkuId, params.get("key")).or().like(SkuInfoEntity::getSkuName, params.get("key"));
        }).and(StringUtils.hasLength((String) params.get("catelogId")) && !"0".equalsIgnoreCase((String) params.get("catelogId")), obj -> {
            obj.eq(SkuInfoEntity::getCatalogId, params.get("catelogId"));
        }).and(StringUtils.hasLength((String) params.get("brandId")) && !"0".equalsIgnoreCase((String) params.get("brandId")), obj -> {
            obj.eq(SkuInfoEntity::getBrandId, params.get("brandId"));
        });
        String min = (String) params.get("min");
        if (StringUtils.hasLength(min)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(min);
                if (bigDecimal.compareTo(BigDecimal.ZERO) >= 0) {
                    wrapper.and(obj -> {
                        obj.ge(SkuInfoEntity::getPrice, bigDecimal);
                    });
                }
            } catch (Exception e) {
            }
        }
        String max = (String) params.get("max");
        if (StringUtils.hasLength(max)) {
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if (bigDecimal.compareTo(BigDecimal.ZERO) == 1) {
                    wrapper.and(obj -> {
                        obj.le(SkuInfoEntity::getPrice, bigDecimal);
                    });
                }
            } catch (Exception e) {
            }
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}