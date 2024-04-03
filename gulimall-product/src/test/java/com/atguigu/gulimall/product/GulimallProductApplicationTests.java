package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallProductApplication.class)
public class GulimallProductApplicationTests {

	@Autowired
	BrandService brandService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	RedissonClient redissonClient;

	@Autowired
	AttrGroupDao attrGroupDao;
	@Test
	public void test() {
		List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(11L, 225L);
		System.out.println(attrGroupWithAttrsBySpuId);
	}
	@Test
	public void redisson() {
		System.out.println(redissonClient);
	}

	@Test
	public void testStringRedisTemplate() {
		ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
		ops.set("hello", "world_" + UUID.randomUUID());
		System.out.println("保存成功");

		String hello = ops.get("hello");
		System.out.println("redis中保存的值是：" + hello);
	}

	@Test
	public void testFindPath() {
		Long[] catelogPath = categoryService.findCatelogPath(225L);
		log.info("完整路径：{}", Arrays.asList(catelogPath));
	}

	@Test
	public void contextLoads() {
//		BrandEntity brandEntity = new BrandEntity();
//		brandEntity.setName("华为");
//		brandService.save(brandEntity);
//		System.out.println("保存成功");

//		BrandEntity brandEntity = new BrandEntity();
//		brandEntity.setBrandId(13L);
//		brandEntity.setDescript("华为");
//		brandService.updateById(brandEntity);

		brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 13L))
				.forEach(System.out::println);
	}


}
