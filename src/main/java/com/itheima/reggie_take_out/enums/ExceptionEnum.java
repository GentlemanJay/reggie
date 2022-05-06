package com.itheima.reggie_take_out.enums;

import lombok.Getter;

/**
 * @author xushengjie
 * @create 2022/4/29 4:46 PM
 */
@Getter
public enum ExceptionEnum {
	//shift + command + u
	PARAM_IS_NULL(10, "传入参数为空"),
	SQL_EXEC_ERROR(11, "数据库执行异常"),
	DATA_NOT_FOUND(12, "数据记录不存在"),
	DATA_DUPLICATE(13, "数据重复异常"),
	FOUND_RELATION_DATA(14, "存在关联数据"),
	CANNOT_REMOVE_SELLING_DISH(15, "无法删除在售数菜品"),
	SHOPPING_CART_ERROR(16, "购物车为空，无法下单"),
	USER_ADDRESS_ERROR(17, "用户地址为空"),
	VALIDATE_FAILED(1002, "参数校验失败"),
	FILENAME_INVALID(1003, "文件名不合法"),
	NO_IMAGE_TYPE(1004, "文件类型不是图片"),
	DATA_INVALID(1005, "数据异常");


	private Integer code;

	private String msg;

	ExceptionEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}
}
