package com.itheima.reggie_take_out.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 * 实现公共字段自动填充
 * @author xushengjie
 * @create 2022/5/1 12:06 PM
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

	/**
	 * 设置新增插入操作，公共字段自动填充
	 * @TableField(fill = FieldFill.INSERT)
	 * @param metaObject
	 */
	@Override
	public void insertFill(MetaObject metaObject) {

		log.info("====新增操作---公共字段自动填充");

		metaObject.setValue("createTime", LocalDateTime.now());
		metaObject.setValue("updateTime", LocalDateTime.now());

		//由于MetaObjectHandler不能获取HttpSession对象，所以不能通过session的方式获取当前用户
		//用户在实现登陆->CRUD操作->公共字段自动填充 这几条链路都属于一个线程
		//获取在用户登陆验证时期赋值给ThreadLocal的用户id
		metaObject.setValue("createUser", BaseContext.getCurrentId());
		metaObject.setValue("updateUser", BaseContext.getCurrentId());

	}

	/**
	 * 设置修改操作，公共字段自动填充
	 * @TableField(fill = FieldFill.INSERT_UPDATE)
	 * @param metaObject
	 */
	@Override
	public void updateFill(MetaObject metaObject) {

		log.info("====更新操作---公共字段自动填充");

		metaObject.setValue("updateTime", LocalDateTime.now());
		metaObject.setValue("updateUser", BaseContext.getCurrentId());
	}
}
