package com.itheima.reggie_take_out.enums;

import lombok.Getter;

/**
 * 员工相关枚举类
 * @author xushengjie
 * @create 2022/4/29 3:53 PM
 */
@Getter
public enum OrderEnum {
	PAY_SUCCESS(5, "下单成功"),
	PAY_FAILED(6, "下单失败");

	private Integer code;

	private String msg;

	OrderEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
