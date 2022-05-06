package com.itheima.reggie_take_out.exception.handler;

import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 自定义全局异常
 * @author xushengjie
 * @create 2022/4/29 4:40 PM
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * 异常处理方法
	 * @return
	 */
	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	@ResponseStatus(HttpStatus.OK)
	public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
		log.error(ex.getMessage());

		if(ex.getMessage().contains("Duplicate entry")){
			return R.error(ExceptionEnum.DATA_DUPLICATE.getCode(), ex.getMessage());
		}

		return R.error(ExceptionEnum.DATA_INVALID.getCode(), ex.getMessage());
	}


	@ExceptionHandler(InternalException.class)
	@ResponseStatus(HttpStatus.OK)
	public R<String> handleInternalException(InternalException e)
	{
		log.error(e.getMessage(), e);
		return R.error(1008, e.getMessage());
	}
}
