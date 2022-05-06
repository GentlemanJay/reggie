package com.itheima.reggie_take_out.enums;

import lombok.Getter;

/**
 * @author xushengjie
 * @create 2022/4/26 8:50 PM
 */
@Getter
public enum LoginEnum {

	LOGIN_FAILED(0, "登陆失败"),
	LOGIN_SUCCESS(1, "登陆成功"),
	USER_NOT_FOUND(2, "用户不存在"),
	PWD_INCORRECT(3, "密码错误"),
	ACCOUNT_FORIDDEN(4, "账号已禁用");

	private Integer code;

	private String msg;

	LoginEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
