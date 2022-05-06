package com.itheima.reggie_take_out.common;

import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * @author xushengjie
 * @create 2022/5/3 11:05 PM
 */
public class FileDelete {

	public static boolean deleteFile(String filepath) {
		try {
			File file = new File(filepath);
			if (file.exists() && file.isFile()) {
				return file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
