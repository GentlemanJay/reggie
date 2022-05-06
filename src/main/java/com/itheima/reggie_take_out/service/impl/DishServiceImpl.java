package com.itheima.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.common.FileDelete;
import com.itheima.reggie_take_out.dto.DishDTO;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.DishFlavor;
import com.itheima.reggie_take_out.entity.Setmeal;
import com.itheima.reggie_take_out.entity.SetmealDish;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.mapper.DishMapper;
import com.itheima.reggie_take_out.service.DishFlavorService;
import com.itheima.reggie_take_out.service.DishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
*
 * @author xushengjie
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService{

	@Autowired
	private DishFlavorService dishFlavorService;

	@Value("${reggie.file-path}")
	private String baseDir;


	/**
	 * 新增菜品和菜品相关口味信息
	 * @param dishDTO
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveDishWithFlavors(DishDTO dishDTO) {

		//保存菜品信息
		this.save(dishDTO);


		//保存菜品口味信息
		List<DishFlavor> flavors = dishDTO.getFlavors();
		flavors.forEach(e -> e.setDishId(dishDTO.getId()));
		dishFlavorService.saveBatch(flavors);


	}


	/**
	 * 删除菜品和菜品相关口味信息
	 * @param ids
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteDishInfo(List<Long> ids) {

		//删除相关套餐图片
		for (Long id : ids) {

			Dish dish = this.getById(id);
			if (dish != null && StringUtils.isNotBlank(dish.getImage())) {
				boolean deleteFile = FileDelete.deleteFile(baseDir + dish.getImage());
				if (!deleteFile) {
					throw new InternalException(ExceptionEnum.DATA_INVALID);
				}
			}

		}

		//判断菜品是否停售，在售菜品不可删除
		LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(Dish::getId, ids);
		queryWrapper.eq(Dish::getStatus, 1);
		//在售菜品的数量
		int sellingCount = this.count(queryWrapper);
		if (sellingCount > 0) {
			throw new InternalException(ExceptionEnum.CANNOT_REMOVE_SELLING_DISH);
		}

		//删除菜品基本信息
		this.removeByIds(ids);

		//删除菜品关联的口味信息
		LambdaQueryWrapper<DishFlavor> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
		dishLambdaQueryWrapper.in(DishFlavor::getDishId, ids);
		dishFlavorService.remove(dishLambdaQueryWrapper);

	}
}
