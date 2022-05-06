package com.itheima.reggie_take_out.dto;

import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.DishFlavor;
import lombok.Data;

import java.util.List;

/**
 * 新增菜品数据 传输对象
 * @author xushengjie
 * @create 2022/5/2 2:25 PM
 */
@Data
public class DishDTO extends Dish {

	/**
	 * 菜品口味集合
	 */
	private List<DishFlavor> flavors;

	/**
	 * 用于显示菜品分类的名称
	 */
	private String categoryName;

}
