package com.itheima.reggie_take_out.enums;

import lombok.Getter;

/**
 * 套餐相关枚举类
 * @author xushengjie
 * @create 2022/4/29 3:53 PM
 */
@Getter
public enum SetmealEnum {

	USER_PARAM_INVALID(0, "参数异常"),
	SAVE_FAILED(1, "新增套餐失败"),
	DELETE_FAILED(2, "删除套餐失败"),
	UPDATE_FAILED(3, "更新套餐失败"),
	SAVE_SUCCESS(4, "新增套餐成功"),
	DELETE_SUCCESS(5, "删除套餐成功"),
	UPDATE_SUCCESS(6, "更新套餐成功");

	private Integer code;

	private String msg;

	SetmealEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
