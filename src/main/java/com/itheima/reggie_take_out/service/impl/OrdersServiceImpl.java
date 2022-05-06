package com.itheima.reggie_take_out.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.common.BaseContext;
import com.itheima.reggie_take_out.entity.*;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.mapper.OrdersMapper;
import com.itheima.reggie_take_out.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author xushengjie
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
implements OrdersService{

	@Autowired
	private OrderDetailService orderDetailService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private UserService userService;

	@Autowired
	private AddressBookService addressBookService;


	/**
	 * 生成订单信息
	 * @param orders
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveOrderInfoWithDetail(Orders orders) {

		//获取当前用户
		Long userId = BaseContext.getCurrentId();

		//获取当前用户的购物车列表
		LambdaQueryWrapper<ShoppingCart> shoppingCartQueryWrapper =
				new LambdaQueryWrapper<>();
		shoppingCartQueryWrapper.eq(ShoppingCart::getUserId, userId);
		List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartQueryWrapper);

		if (CollectionUtil.isEmpty(shoppingCartList)) {
			throw new InternalException(ExceptionEnum.DATA_INVALID);
		}

		//查询用户信息
		User user = userService.getById(userId);

		//查询用户的地址
		Long addressBookId = orders.getAddressBookId();
		AddressBook addressBook = addressBookService.getById(addressBookId);
		if (addressBook == null) {
			throw new InternalException(ExceptionEnum.USER_ADDRESS_ERROR);
		}

		long orderId = RandomUtil.randomLong();


		//获取订单总金额
		//原子整型类
		AtomicInteger totalAmount = new AtomicInteger(0);

		List<OrderDetail> orderDetailList = shoppingCartList.stream().map(e -> {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrderId(orderId);
			BeanUtils.copyProperties(e, orderDetail);

			//循环累加金额
			totalAmount.addAndGet(e.getAmount().multiply(new BigDecimal(e.getNumber())).intValue());
			return orderDetail;
		}).collect(Collectors.toList());


		//拼接用户地址
		String userAddress = (StringUtils.isBlank(addressBook.getProvinceName()) ? "" : addressBook.getProvinceName()) +
				(StringUtils.isBlank(addressBook.getCityName()) ? "" : addressBook.getCityName()) +
				(StringUtils.isBlank(addressBook.getDistrictName()) ? "" : addressBook.getDistrictName()) +
				(StringUtils.isBlank(addressBook.getDetail()) ? "" : addressBook.getDetail());


		//生成订单信息
		orders.setNumber(String.valueOf(orderId))
				.setStatus(2)
				.setUserId(userId)
				.setOrderTime(LocalDateTime.now())
				.setCheckoutTime(LocalDateTime.now())
				.setAmount(new BigDecimal(totalAmount.get()))
				.setPhone(user.getPhone())
				.setUserName(user.getName())
				.setConsignee(addressBook.getConsignee())
				.setAddress(userAddress);

		this.save(orders);


		//生成对应的订单详情数据
		orderDetailService.saveBatch(orderDetailList);

		//最后下单完成后清空购物车
		shoppingCartService.remove(shoppingCartQueryWrapper);

	}
}
