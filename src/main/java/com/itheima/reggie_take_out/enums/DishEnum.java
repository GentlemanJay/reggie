package com.itheima.reggie_take_out.enums;

import lombok.Getter;

/**
 * 菜品相关枚举类
 * @author xushengjie
 * @create 2022/4/29 3:53 PM
 */
@Getter
public enum DishEnum {

	USER_PARAM_INVALID(0, "参数异常"),
	SAVE_FAILED(1, "新增菜品失败"),
	DELETE_FAILED(2, "删除菜品失败"),
	UPDATE_FAILED(3, "更新菜品失败"),
	SAVE_SUCCESS(4, "新增菜品成功"),
	DELETE_SUCCESS(5, "删除菜品成功"),
	UPDATE_SUCCESS(6, "更新菜品成功");

	private Integer code;

	private String msg;

	DishEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
