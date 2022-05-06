package com.itheima.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie_take_out.entity.Orders;

/**
 * @author xushengjie
 */
public interface OrdersService extends IService<Orders> {

	/**
	 * 生成订单信息
	 * @param orders
	 */
	void saveOrderInfoWithDetail(Orders orders);
}
