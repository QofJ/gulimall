package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 *  封装页面所有可能传递过来的查询条件
 */
@Data
public class SearchParam {
    private String keyword; // 页面传递过来的全文匹配关键字
    private Long catalog3Id; // 三级分类id
    /**
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort; // 排序条件
    /**
     * hasStock=0/1 是否有货、 skuPrice区间、brandId、catalog3Id、attrs\
     * hasStock=0/1
     * skuPrice=1_500/_500/500_
     */
    private Integer hasStock; // 是否只显示有货
    private String skuPrice; // 价格区间查询
    private List<Long> brandId; // 按照品牌进行查询，可以多选
    private List<String> attrs; // 按照属性进行筛选
    private Integer pageNum = 1; // 页码

    private String _queryString; // 原生的所有查询条件
}
