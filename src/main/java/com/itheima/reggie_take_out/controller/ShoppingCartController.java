package com.itheima.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie_take_out.common.BaseContext;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.ShoppingCart;
import com.itheima.reggie_take_out.enums.ShoppingCartEnum;
import com.itheima.reggie_take_out.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xushengjie
 * @create 2022/5/4 9:18 PM
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;


	/**
	 * 添加购物车
	 * @param shoppingCart
	 * @return
	 */
	@PostMapping("/add")
	public R<ShoppingCart> addInShoppingCart(@RequestBody ShoppingCart shoppingCart) {

		//获取当前用户
		Long userId = BaseContext.getCurrentId();
		shoppingCart.setUserId(userId);

		//查询当前加入的菜品或者套餐是否已经在购物车中，如果已经存在则数量更新，不存在则新增

		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(shoppingCart.getUserId() != null, ShoppingCart::getUserId, userId);
		if (shoppingCart.getDishId() != null) {
			//加购的是菜品，根据用户id和菜品id查询是否存在相同数据
			queryWrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId());


		} else {
			//加购的是套餐
			queryWrapper.eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
		}

		//查询数据
		ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
		if (cartServiceOne != null) {
			//更新
			cartServiceOne.setNumber(cartServiceOne.getNumber() + 1);
			shoppingCartService.updateById(cartServiceOne);

		} else {
			//新增
			shoppingCart.setCreateTime(LocalDateTime.now());
			shoppingCartService.save(shoppingCart);
			cartServiceOne = shoppingCart;

		}

		return R.success(cartServiceOne);

	}


	/**
	 * 清空购物车
	 * @return
	 */
	@DeleteMapping("/clean")
	public R<String> cleanShoppingCart() {
		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

		shoppingCartService.remove(queryWrapper);

		return R.success(ShoppingCartEnum.DELETE_SUCCESS.getMsg());

	}



	/**
	 * 获取当前用户的购物车列表数据
	 * @return
	 */
	@GetMapping("/list")
	public R<List<ShoppingCart>> listShoppingCart() {

		//获取当前用户
		Long userId = BaseContext.getCurrentId();

		LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(ShoppingCart::getUserId, userId);
		queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
		List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);

		return R.success(shoppingCarts);

	}

}
