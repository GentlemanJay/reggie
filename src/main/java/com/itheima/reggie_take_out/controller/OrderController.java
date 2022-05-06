package com.itheima.reggie_take_out.controller;

import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.Orders;
import com.itheima.reggie_take_out.enums.OrderEnum;
import com.itheima.reggie_take_out.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xushengjie
 * @create 2022/5/5 3:41 PM
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

	@Autowired
	private OrdersService ordersService;


	@PostMapping("/submit")
	public R<String> addOrderInfo(@RequestBody Orders orders) {

		ordersService.saveOrderInfoWithDetail(orders);

		return R.success(OrderEnum.PAY_SUCCESS.getMsg());
	}

}
