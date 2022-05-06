package com.itheima.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_take_out.dto.SetmealDTO;
import com.itheima.reggie_take_out.entity.Setmeal;

import java.util.List;

/**
 * @author xushengjie
 */
public interface SetmealService extends IService<Setmeal> {

	/**
	 * 新增套餐信息和套餐关联的菜品信息
	 * @param setmealDTO
	 */
	void saveSetmealWithDishes(SetmealDTO setmealDTO);

	/**
	 * 删除套餐信息和套餐关联的菜品信息
	 * @param ids
	 */
	void deleteSetmealInfo(List<Long> ids);
}
