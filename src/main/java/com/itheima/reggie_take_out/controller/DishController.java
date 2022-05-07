package com.itheima.reggie_take_out.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.FileDelete;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.dto.DishDTO;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.DishFlavor;
import com.itheima.reggie_take_out.enums.DishEnum;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.DishFlavorService;
import com.itheima.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xushengjie
 * @create 2022/5/1 9:59 PM
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

	@Autowired
	private DishService dishService;

	@Autowired
	private DishFlavorService dishFlavorService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Value("${reggie.file-path}")
	private String baseDir;


	/**
	 * 新增菜品
	 * @param dishDTO
	 * @return
	 */
	@PostMapping
	public R<String> saveDishInfo(@RequestBody DishDTO dishDTO) {

		dishService.saveDishWithFlavors(dishDTO);

		//删除缓存
		String key = "dish:" + dishDTO.getCategoryId() + ":" + dishDTO.getStatus();
		redisTemplate.delete(key);
		log.info("=====update cache");

		return R.success(DishEnum.SAVE_SUCCESS.getMsg());
	}


	/**
	 * 修改菜品
	 * @param dishDTO
	 * @return
	 */
	@PutMapping
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public R<String> updateDishInfo(@RequestBody DishDTO dishDTO) {

		//修改菜品信息
		dishService.updateById(dishDTO);


		//删除对应的菜品口味信息
		LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DishFlavor::getDishId, dishDTO.getId());
		dishFlavorService.remove(queryWrapper);

		//重新插入对应菜品口味信息列表
		List<DishFlavor> flavors = dishDTO.getFlavors();
		if (!CollectionUtil.isEmpty(flavors)) {
			flavors = flavors.stream()
					.map(e -> {
						e.setDishId(dishDTO.getId());
						return e;
					}).collect(Collectors.toList());
			dishFlavorService.saveBatch(flavors);
		}


		//删除缓存
		String key = "dish:" + dishDTO.getCategoryId() + ":" + dishDTO.getStatus();
		redisTemplate.delete(key);
		log.info("=====update cache");

		return R.success(DishEnum.UPDATE_SUCCESS.getMsg());
	}


	/**
	 * 根据菜品id获取菜品信息
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public R<DishDTO> getDishInfoById(@PathVariable Long id) {
		DishDTO dishDTO = new DishDTO();

		//菜品信息
		Dish dish = dishService.getById(id);
		BeanUtils.copyProperties(dish, dishDTO);

		//菜品口味信息
		LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(DishFlavor::getDishId, dish.getId());
		List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper);
		dishDTO.setFlavors(dishFlavors);

		return R.success(dishDTO);
	}



	/**
	 * 分页显示菜品管理数据
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/page")
	public R<Page<DishDTO>> page(Integer page, Integer pageSize, String name) {

		//创建分页构造器
		Page<Dish> objectPage = new Page<>(page, pageSize);

		//创建查询条件构造器
		LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(name), Dish::getName, name);

		//根据sort字段进行升序排序
		queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

		//执行分页查询操作
		dishService.page(objectPage, queryWrapper);


		//新建Page对象，获取对应的categoryName数据
		Page<DishDTO> dtoPage = new Page<>();
		//数据拷贝，排除records字段
		BeanUtils.copyProperties(objectPage, dtoPage, "records");

		List<Dish> records = objectPage.getRecords();
		List<DishDTO> dtoList = records.stream().map(e -> {
			DishDTO dishDTO = new DishDTO();
			BeanUtils.copyProperties(e, dishDTO);
			Long categoryId = e.getCategoryId();
			Category category = categoryService.getById(categoryId);
			if (category != null) {
				dishDTO.setCategoryName(category.getName());
			}
			return dishDTO;
		}).collect(Collectors.toList());

		dtoPage.setRecords(dtoList);

		return R.success(dtoPage);
	}


	/**
	 * 批量停售菜品
	 * @param ids
	 * @return
	 */
	@PostMapping("/status/0")
	public R<String> soldDishByIds(@RequestParam("ids") List<Long> ids) {

		List<Dish> list = new ArrayList<>();
		for (Long id : ids) {
			Dish dish = dishService.getById(id);
			dish.setStatus(0);
			list.add(dish);
		}

		dishService.updateBatchById(list);


		//根据任意id获取菜品实体
		Dish dish = dishService.getById(ids.get(0));

		if (dish != null) {
			//删除缓存
			String key = "dish:" + dish.getCategoryId() + ":" + dish.getStatus();
			redisTemplate.delete(key);
			log.info("=====update cache");
		}

		return R.success(DishEnum.UPDATE_SUCCESS.getMsg());
	}



	/**
	 * 批量启售菜品
	 * @param ids
	 * @return
	 */
	@PostMapping("/status/1")
	public R<String> resellDishByIds(@RequestParam("ids") List<Long> ids) {

		List<Dish> list = new ArrayList<>();
		for (Long id : ids) {
			Dish dish = dishService.getById(id);
			dish.setStatus(1);
			list.add(dish);
		}

		dishService.updateBatchById(list);

		//根据任意id获取菜品实体
		Dish dish = dishService.getById(ids.get(0));

		if (dish != null) {
			//删除缓存
			String key = "dish:" + dish.getCategoryId() + ":" + dish.getStatus();
			redisTemplate.delete(key);
			log.info("=====update cache");
		}

		return R.success(DishEnum.UPDATE_SUCCESS.getMsg());
	}



	/**
	 * 批量删除菜品
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public R<String> removeDishByIds(@RequestParam("ids") List<Long> ids) {

		dishService.deleteDishInfo(ids);

		//根据任意id获取菜品实体
		Dish dish = dishService.getById(ids.get(0));

		if (dish != null) {
			//删除缓存
			String key = "dish:" + dish.getCategoryId() + ":" + dish.getStatus();
			redisTemplate.delete(key);
			log.info("=====update cache");
		}

		return R.success(DishEnum.DELETE_SUCCESS.getMsg());
	}


	/**
	 * 根据条件获取菜品信息
	 * @param dish
	 * @return
	 */
	@GetMapping("/list")
	public R<List<DishDTO>> getDishListByCategoryId(Dish dish) {

		List<DishDTO> dishDTOList = new ArrayList<>();

		//根据菜品类别和状态进行缓存
		String key = "dish:" + dish.getCategoryId() + ":" + dish.getStatus();

		//从redis中获取缓存数据
		dishDTOList = (List<DishDTO>) redisTemplate.opsForValue().get(key);

		if (!CollectionUtil.isEmpty(dishDTOList)) {
			log.info("===data from redis");
			return R.success(dishDTOList);
		}


		//缓存没有对应数据，则查询数据库

		LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

		//根据categoryId获取菜品分类下的所有菜品集合
		queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());

		//查询启售的菜品 status == 1
		queryWrapper.eq(dish.getStatus() != null, Dish::getStatus, 1);


		//根据菜品名称name模糊匹配菜品信息
		queryWrapper.like(StringUtils.isNotBlank(dish.getName()), Dish::getName, dish.getName());


		//排序条件
		queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
		List<Dish> dishList = dishService.list(queryWrapper);

		dishDTOList = dishList.stream().map(e -> {
			DishDTO dishDTO = new DishDTO();
			BeanUtils.copyProperties(e, dishDTO);

			//获取category对象
			Category category = categoryService.getById(e.getCategoryId());
			if (category != null) {
				dishDTO.setCategoryName(category.getName());
			}

			//查询菜品对应的口味信息列表数据
			LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
			queryWrapper1.eq(DishFlavor::getDishId, e.getId());
			List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
			dishDTO.setFlavors(dishFlavors);
			return dishDTO;
		}).collect(Collectors.toList());

		//更新至缓存中
		if (!CollectionUtil.isEmpty(dishDTOList)) {
			log.info("====insert data into redis");
			redisTemplate.opsForValue().set(key, dishDTOList, 60, TimeUnit.MINUTES);
		}

		return R.success(dishDTOList);
	}
}
