package com.itheima.reggie_take_out.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.entity.Dish;
import com.itheima.reggie_take_out.entity.Setmeal;
import com.itheima.reggie_take_out.enums.CategoryEnum;
import com.itheima.reggie_take_out.enums.EmployeeEnum;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.service.CategoryService;
import com.itheima.reggie_take_out.service.DishService;
import com.itheima.reggie_take_out.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xushengjie
 * @create 2022/4/30 5:51 PM
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private DishService dishService;

	@Autowired
	private SetmealService setmealService;


	/**
	 * 新增菜品分类或套餐分类
	 * @param category
	 * @return
	 */
	@PostMapping
	public R<String> saveCategory(@RequestBody Category category) {
		if (category == null) {
			return R.error(EmployeeEnum.USER_PARAM_INVALID.getMsg());
		}
		boolean save = categoryService.save(category);
		if (!save) {
			throw new InternalException(ExceptionEnum.SQL_EXEC_ERROR);
		}

		return R.success(CategoryEnum.SAVE_SUCCESS.getMsg());
	}


	/**
	 * 更新分类
	 * @param category
	 * @return
	 */
	@PutMapping
	public R<String> updateCategory(@RequestBody Category category) {
		if (category == null) {
			return R.error(EmployeeEnum.USER_PARAM_INVALID.getMsg());
		}

		boolean updateById = categoryService.updateById(category);
		if (!updateById) {
			throw new InternalException(ExceptionEnum.SQL_EXEC_ERROR);
		}

		return R.success(CategoryEnum.UPDATE_SUCCESS.getMsg());

	}


	/**
	 * 根据id删除分类数据
	 * 删除分类信息前，需要判断该分类下是否关联菜品信息或者套餐信息（分别对应Dish和Setmeal）
	 * @param ids
	 * @return
	 */
	@DeleteMapping
	public R<String> removeCategory(@RequestParam("ids") Long ids) {

		//查询当前分类是否存在
		Category category = categoryService.getById(ids);
		if (category == null) {
			throw new InternalException(ExceptionEnum.DATA_NOT_FOUND);
		}

		//判断该分类下是否关联菜品信息
		if (category.getType() == 1) {

			LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(Dish::getCategoryId, ids);
			List<Dish> dishList = dishService.list(queryWrapper);
			if (!CollectionUtil.isEmpty(dishList)) {
				throw new InternalException(ExceptionEnum.FOUND_RELATION_DATA);
			}


		}

		//判断该分类下是否关联套餐信息
		if (category.getType() == 2) {

			LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(Setmeal::getCategoryId, ids);
			List<Setmeal> setmealList = setmealService.list(queryWrapper);
			if (!CollectionUtil.isEmpty(setmealList)) {
				throw new InternalException(ExceptionEnum.FOUND_RELATION_DATA);
			}
		}

		boolean removeById = categoryService.removeById(ids);
		if (!removeById) {
			throw new InternalException(ExceptionEnum.SQL_EXEC_ERROR);
		}

		return R.success(CategoryEnum.DELETE_SUCCESS.getMsg());

	}



	/**
	 * 分页显示分类管理数据
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@GetMapping("/page")
	public R<Page<Category>> page(Integer page, Integer pageSize) {

		//创建分页构造器
		Page<Category> objectPage = new Page<>(page, pageSize);

		//创建查询条件构造器
		LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

		//根据sort字段进行升序排序
		queryWrapper.orderByAsc(Category::getSort);

		//执行分页查询操作
		categoryService.page(objectPage, queryWrapper);

		return R.success(objectPage);
	}


	/**
	 * 获取菜品分类或套餐分类枚举数据
	 * @return
	 */
	@GetMapping("/list")
	public R<List<Category>> listCategoryType(Category category) {
		LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(category.getType() != null, Category::getType,category.getType());
		queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
		List<Category> categoryList = categoryService.list(queryWrapper);
		return R.success(categoryList);

	}

}
