package com.itheima.reggie_take_out.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前用户的信息
 * @author xushengjie
 * @create 2022/5/1 2:02 PM
 */
public class BaseContext {

	/**
	 * 创建ThreadLocal实例
	 */
	private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();


	/**
	 * 设置id值
	 * @param id
	 */
	public static void setCurrentId(Long id) {
		threadLocal.set(id);
	}


	/**
	 * 获取id值
	 * @return
	 */
	public static Long getCurrentId() {
		return threadLocal.get();
	}
}
