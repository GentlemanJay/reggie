package com.itheima.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_take_out.dto.DishDTO;
import com.itheima.reggie_take_out.entity.Dish;

import java.util.List;

/**
 * @author xushengjie
 */
public interface DishService extends IService<Dish> {


	/**
	 * 新增菜品和菜品相关口味信息
	 * @param dishDTO
	 */
	void saveDishWithFlavors(DishDTO dishDTO);

	/**
	 * 删除菜品和菜品相关口味信息
	 * @param ids
	 */
	void deleteDishInfo(List<Long> ids);
}
