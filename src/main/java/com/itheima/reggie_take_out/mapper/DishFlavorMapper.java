package com.itheima.reggie_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie_take_out.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
* @Entity com.itheima.reggie_take_out.entity.DishFlavor
*/
@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {


	void deleteByDishId(Long dishId);

}
