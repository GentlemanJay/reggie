package com.itheima.reggie_take_out.dto;

import com.itheima.reggie_take_out.entity.User;
import lombok.Data;

/**
 * @author xushengjie
 * @create 2022/5/4 5:46 PM
 */
@Data
public class UserDTO extends User {
	private String code;
}
