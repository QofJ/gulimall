package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);

        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid().equals(0L)
        ).map(menu -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

//    @CacheEvict(value = "category", key = "'getLevel1Categories'")
    @Caching(evict = {
            @CacheEvict(value = "category", key = "'getLevel1Categories'"),
            @CacheEvict(value = "category", key = "'getCatalogJson'")
    })
//    @CacheEvict(value = "category", allEntries = true)
//    @CachePut
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());


    }

    @Cacheable(value = {"category", "product"}, key = "'getLevel1Categories'")
//    @Cacheable(value = {"category", "product"}, key = "#root.method.name")
    @Override
    public List<CategoryEntity> getLevel1Categories() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Cacheable(value = {"category"}, key = "#root.methodName"/*, sync = true*/)
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库...");
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null && categoryEntities.size() > 0) {
                catelog2Vos = categoryEntities.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());

                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, item.getCatId());
                    if (level3Catelog != null && level3Catelog.size() > 0) {
                        List<Catelog2Vo.Catalog3Vo> catalog3Vos = level3Catelog.stream().map(level3 -> {
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(item.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parent_cid;
    }
    // TODO 产生堆外内存溢出：
    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();
            String s = JSON.toJSONString(catalogJsonFromDb);
            redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
            return catalogJsonFromDb;
        }
        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });
        return result;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        //1、锁的名字，锁的粒度，越细越快
        RLock lock = redisson.getLock("CatalogJson-lock");
        lock.lock();

        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDb;

    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
//            redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> dataFromDb = null;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);//Arrays.asList("lock") 代表传入的key，uuid代表传入的value，Integer.class代表返回值类型<
            }

            //lua脚本， 保证原子性
//            String lockValue = redisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lockValue)) {
//                redisTemplate.delete("lock");
//            }
            return dataFromDb;
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDbWithRedisLock();
        }


    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }

        List<CategoryEntity> selectList = baseMapper.selectList(null);

        List<CategoryEntity> level1Categories = getParent_cid(selectList, 0L);
        Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null && categoryEntities.size() > 0) {
                catelog2Vos = categoryEntities.stream().map(item -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, item.getCatId().toString(), item.getName());

                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, item.getCatId());
                    if (level3Catelog != null && level3Catelog.size() > 0) {
                        List<Catelog2Vo.Catalog3Vo> catalog3Vos = level3Catelog.stream().map(level3 -> {
                            Catelog2Vo.Catalog3Vo catalog3Vo = new Catelog2Vo.Catalog3Vo(item.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(catalog3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {

        synchronized (this) {
            return getDataFromDb();
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        return selectList.stream().filter(item -> item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }


    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));
                    return categoryEntity;
                }).sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());
        return children;
    }

}