package com.itheima.reggie_take_out.enums;

import lombok.Getter;

/**
 * 员工相关枚举类
 * @author xushengjie
 * @create 2022/4/29 3:53 PM
 */
@Getter
public enum EmployeeEnum {

	USER_PARAM_INVALID(0, "参数异常"),
	SAVE_FAILED(1, "新增员工失败"),
	DELETE_FAILED(2, "删除员工失败"),
	UPDATE_FAILED(3, "更新员工失败"),
	SAVE_SUCCESS(4, "新增员工成功"),
	DELETE_SUCCESS(5, "删除员工成功"),
	UPDATE_SUCCESS(6, "更新员工成功");

	private Integer code;

	private String msg;

	EmployeeEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
