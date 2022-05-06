package com.itheima.reggie_take_out.dto;

import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.Setmeal;
import com.itheima.reggie_take_out.entity.SetmealDish;
import lombok.Data;

import java.util.List;

/**
 * @author xushengjie
 * @create 2022/5/3 3:47 PM
 */
@Data
public class SetmealDTO extends Setmeal {

	private List<SetmealDish> setmealDishes;

	private String categoryName;

}
