package com.itheima.reggie_take_out.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.common.FileDelete;
import com.itheima.reggie_take_out.dto.SetmealDTO;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.Setmeal;
import com.itheima.reggie_take_out.entity.SetmealDish;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.mapper.SetmealMapper;
import com.itheima.reggie_take_out.service.SetmealDishService;
import com.itheima.reggie_take_out.service.SetmealService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xushengjie
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{

	@Autowired
	private SetmealDishService setmealDishService;

	@Value("${reggie.file-path}")
	private String baseDir;


	/**
	 * 实现套餐的插入和套餐关联菜品的插入
	 * @param setmealDTO
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveSetmealWithDishes(SetmealDTO setmealDTO) {

		if (setmealDTO == null) {
			throw new InternalException(ExceptionEnum.PARAM_IS_NULL);
		}


		//插入套餐信息
		this.save(setmealDTO);


		//插入套餐关联的菜品信息
		List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

		//setmealId特殊赋值
		if (!CollectionUtil.isEmpty(setmealDishes)) {

			setmealDishes = setmealDishes.stream().map(e -> {
				e.setSetmealId(setmealDTO.getId());
				return e;
			}).collect(Collectors.toList());

			setmealDishService.saveBatch(setmealDishes);


		}


	}

	/**
	 * 删除套餐信息和套餐关联的菜品信息
	 * @param ids
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteSetmealInfo(List<Long> ids) {

		//删除相关套餐图片
		for (Long id : ids) {

			Setmeal setmeal = this.getById(id);
			if (setmeal != null && StringUtils.isNotBlank(setmeal.getImage())) {
				boolean deleteFile = FileDelete.deleteFile(baseDir + setmeal.getImage());
				if (!deleteFile) {
					throw new InternalException(ExceptionEnum.DATA_INVALID);
				}
			}

		}

		//判断套餐时候停售，在售套餐不可删除
		LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.in(Setmeal::getId, ids);
		queryWrapper.eq(Setmeal::getStatus, 1);
		//在售套餐的数量
		int sellingCount = this.count(queryWrapper);
		if (sellingCount > 0) {
			throw new InternalException(ExceptionEnum.CANNOT_REMOVE_SELLING_DISH);
		}

		//删除套餐基本信息
		this.removeByIds(ids);

		//删除套餐关联的菜品信息
		LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
		dishLambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
		setmealDishService.remove(dishLambdaQueryWrapper);

	}
}
