package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallProductApplication.class)
public class GulimallProductApplicationTests {

	@Autowired
	BrandService brandService;

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
