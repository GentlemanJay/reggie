package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.dto.DishDTO;
import com.itheima.reggie_take_out.dto.SetmealDTO;
import com.itheima.reggie_take_out.entity.*;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.enums.SetmealEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.SetmealDishService;
import com.itheima.reggie_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xushengjie
 * @create 2022/5/3 10:10 AM
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

	@Autowired
	private SetmealService setmealService;

	@Autowired
	private SetmealDishService setmealDishService;

	@Autowired
	private CategoryService categoryService;


	/**
	 * 新增套餐
	 * @param setmealDTO
	 * @return
	 */
	@PostMapping
	public R<String> saveSetmealInfo(@RequestBody SetmealDTO setmealDTO) {
		setmealService.saveSetmealWithDishes(setmealDTO);
		return R.success(SetmealEnum.SAVE_SUCCESS.getMsg());

	}



	/**
	 * 分页显示套餐数据
	 * @param page
	 * @param pageSize
	 * @param name
	 * @return
	 */
	@GetMapping("/page")
	public R<Page<SetmealDTO>> page(Integer page, Integer pageSize, String name) {

		//创建分页构造器
		Page<Setmeal> objectPage = new Page<>(page, pageSize);

		Page<SetmealDTO> setmealDTOPage = new Page<>();

		//创建查询条件构造器
		LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(name), Setmeal::getName, name);

		//根据更新时间字段进行降序排序
		queryWrapper.orderByDesc(Setmeal::getUpdateTime);

		//执行分页查询操作
		setmealService.page(objectPage, queryWrapper);

		//属性复制
		BeanUtils.copyProperties(objectPage, setmealDTOPage, "records");

		//获取元数据
		List<Setmeal> records = objectPage.getRecords();

		List<SetmealDTO> setmealDTOS = records.stream().map(e -> {
			SetmealDTO setmealDTO = new SetmealDTO();
			BeanUtils.copyProperties(e, setmealDTO);
			Category category = categoryService.getById(e.getCategoryId());
			if (category != null) {
				setmealDTO.setCategoryName(category.getName());
			}
			return setmealDTO;
		}).collect(Collectors.toList());

		setmealDTOPage.setRecords(setmealDTOS);


		return R.success(setmealDTOPage);
	}


	/**
	 * 根据套餐id获取套餐信息
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public R<SetmealDTO> getSetmealInfoById(@PathVariable Long id) {

		SetmealDTO setmealDTO = new SetmealDTO();

		//获取套餐基础信息
		Setmeal setmeal = setmealService.getById(id);
		BeanUtils.copyProperties(setmeal, setmealDTO);

		//获取套餐关联的菜品信息列表
		LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SetmealDish::getSetmealId, id);
		List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);

		setmealDTO.setSetmealDishes(setmealDishes);


		return R.success(setmealDTO);

	}


	/**
	 * 修改套餐
	 * @param setmealDTO
	 * @return
	 */
	@PutMapping
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public R<String> updateSetmealInfo(@RequestBody SetmealDTO setmealDTO) {

		if (setmealDTO == null) {
			throw new InternalException(ExceptionEnum.PARAM_IS_NULL);
		}

		//修改套餐基础信息
		setmealService.updateById(setmealDTO);


		//根据套餐id删除对应的套餐关联菜品信息
		LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SetmealDish::getSetmealId, setmealDTO.getId());
		setmealDishService.remove(queryWrapper);

		//重新插入套餐关联菜品信息
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
		//setmealId特殊处理

		setmealDishes = setmealDishes.stream().map(e -> {
			e.setSetmealId(setmealDTO.getId());
			return e;
		}).collect(Collectors.toList());

		setmealDishService.saveBatch(setmealDishes);


		return R.success(SetmealEnum.UPDATE_SUCCESS.getMsg());
	}





	/**
	 * 批量停售套餐
	 * @param ids
	 * @return
	 */
	@PostMapping("/status/0")
	public R<String> soldSetmealByIds(@RequestParam("ids") Long[] ids) {

		List<Setmeal> list = new ArrayList<>();
		for (Long id : ids) {
			Setmeal setmeal = setmealService.getById(id);
			setmeal.setStatus(0);
			list.add(setmeal);
		}

		setmealService.updateBatchById(list);

		return R.success(SetmealEnum.UPDATE_SUCCESS.getMsg());
	}



	/**
	 * 批量启售套餐
	 * @param ids
	 * @return
	 */
	@PostMapping("/status/1")
	public R<String> resellSetmealByIds(@RequestParam("ids") Long[] ids) {

		List<Setmeal> list = new ArrayList<>();
		for (Long id : ids) {
			Setmeal setmeal = setmealService.getById(id);
			setmeal.setStatus(1);
			list.add(setmeal);
		}

		setmealService.updateBatchById(list);

		return R.success(SetmealEnum.UPDATE_SUCCESS.getMsg());
	}


	/**
	 * 删除套餐信息和套餐关联菜品信息
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	public R<String> deleteSetmealInfo(@RequestParam("ids") List<Long> ids) {

		setmealService.deleteSetmealInfo(ids);

		return R.success(SetmealEnum.DELETE_SUCCESS.getMsg());
	}



	/**
	 * 根据条件获取套餐信息
	 * @param setmeal
	 * @return
	 */
	@GetMapping("/list")
	public R<List<SetmealDTO>> getSetmealListByCategoryId(Setmeal setmeal) {

		List<SetmealDTO> setmealDTOS = new ArrayList<>();

		LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

		//根据categoryId获取套餐分类下的所有菜品集合
		queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());

		//查询启售的菜品 status == 1
		queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, 1);


		//根据菜品名称name模糊匹配菜品信息
		queryWrapper.like(StringUtils.isNotBlank(setmeal.getName()), Setmeal::getName, setmeal.getName());


		//排序条件
		queryWrapper.orderByDesc(Setmeal::getUpdateTime);
		List<Setmeal> setmealList = setmealService.list(queryWrapper);

		setmealDTOS = setmealList.stream().map(e -> {
			SetmealDTO setmealDTO = new SetmealDTO();
			BeanUtils.copyProperties(e, setmealDTO);

			//获取category对象
			Category category = categoryService.getById(e.getCategoryId());
			if (category != null) {
				setmealDTO.setCategoryName(category.getName());
			}

			//查询菜品对应的口味信息列表数据
			LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
			queryWrapper1.eq(SetmealDish::getSetmealId, e.getId());
			List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper1);
			setmealDTO.setSetmealDishes(setmealDishes);
			return setmealDTO;
		}).collect(Collectors.toList());

		return R.success(setmealDTOS);
	}



}
