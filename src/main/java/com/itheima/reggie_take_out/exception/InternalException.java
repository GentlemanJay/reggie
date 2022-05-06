package com.itheima.reggie_take_out.exception;

import com.itheima.reggie_take_out.enums.ExceptionEnum;

/**
 * @author xushengjie
 * @create 2022/4/29 4:47 PM
 */
public class InternalException extends RuntimeException {

	public InternalException(ExceptionEnum exceptionEnum) {
		super(exceptionEnum.getMsg());
	}
}
