package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService imagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private ThreadPoolExecutor executor;
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

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        }, executor);

        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, executor);

        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);

        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);

        CompletableFuture.allOf(saleAttrFuture, descFuture, baseAttrFuture, imageFuture).get();
        return skuItemVo;

//        SkuInfoEntity info = getById(skuId);
//        skuItemVo.setInfo(info);
//        Long catalogId = info.getCatalogId();
//        Long spuId = info.getSpuId();
//        List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
//        skuItemVo.setImages(images);
//
//        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
//        skuItemVo.setSaleAttr(saleAttrVos);
//
//        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
//        skuItemVo.setDesp(spuInfoDescEntity);
//
//        List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
//        skuItemVo.setGroupAttrs(attrGroupVos);
//        return skuItemVo;
    }

}