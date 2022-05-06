package com.itheima.reggie_take_out.enums;

import lombok.Getter;

/**
 * 员工相关枚举类
 * @author xushengjie
 * @create 2022/4/29 3:53 PM
 */
@Getter
public enum UserEnum {

	USER_PARAM_INVALID(0, "参数异常"),
	SEND_MSG_FAILED(1, "验证码发送失败"),
	SEND_MSG_SUCCESS(2, "验证码发送成功"),
	USER_LOGIN_SUCCESS(3, "用户登陆成功"),
	USER_LOGIN_FAILED(4, "用户登陆失败"),
	USER_PAY_SUCCESS(5, "用户支付成功"),
	USER_PAY_FAILED(6, "用户支付失败");

	private Integer code;

	private String msg;

	UserEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
