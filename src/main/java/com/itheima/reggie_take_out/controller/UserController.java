package com.itheima.reggie_take_out.controller;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.config.TencentCloudParamConfig;
import com.itheima.reggie_take_out.dto.UserDTO;
import com.itheima.reggie_take_out.entity.User;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.enums.UserEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.service.UserService;
import com.itheima.reggie_take_out.utils.SendMsgUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xushengjie
 * @create 2022/5/4 4:18 PM
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private TencentCloudParamConfig tcp;

	@Autowired
	private RedisTemplate redisTemplate;


	/**
	 * 发送登陆验证码
	 * @param user
	 * @return
	 */
	@PostMapping("/sendMsg")
	public R<String> sendCode(@RequestBody User user) {
		//1.获取手机号
		String phone = user.getPhone();

		if (StringUtils.isNotBlank(phone)) {
			//2.生成随机数
			String code = RandomUtil.randomNumbers(6);
			log.info("===code: " + code);

			//3.调用腾讯云短信SMS服务
//			SendMsgUtil.sendMsg(phone, code, tcp.getSdkAppId(), tcp.getSignName(), tcp.getTemplateId());

			//4.将生成的验证码保存到redis中,并设置过期时间为5分钟
			redisTemplate.opsForValue().set(phone, code, 300, TimeUnit.SECONDS);

			return R.success(UserEnum.SEND_MSG_SUCCESS.getMsg());
		}


		return R.error(UserEnum.SEND_MSG_FAILED.getMsg());
	}


	/**
	 * 用户登录
	 * @param userDTO
	 * @return
	 */
	@PostMapping("/login")
	public R<User> login(@RequestBody UserDTO userDTO) {

		//参数校验
		if (userDTO == null || StringUtils.isBlank(userDTO.getPhone())
				|| StringUtils.isBlank(userDTO.getCode())) {

			throw new InternalException(ExceptionEnum.PARAM_IS_NULL);
		}

		//验证码校对
		String backendCode = redisTemplate.opsForValue().get(userDTO.getPhone()).toString();
		if (!Objects.equals(backendCode, userDTO.getCode())) {
			throw new InternalException(ExceptionEnum.DATA_INVALID);
		}

		//根据手机号的唯一性查询用户是否已经存在，若不存在则插入用户表
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(User::getPhone, userDTO.getPhone());
		User user = userService.getOne(queryWrapper);
		if (user == null) {
			user = new User();
			user.setPhone(userDTO.getPhone());
			//生成随机字符串用作初始用户名
			user.setName(RandomUtil.randomString(5));
			boolean save = userService.save(user);
			if (!save) {
				throw new InternalException(ExceptionEnum.SQL_EXEC_ERROR);
			}
		}

		//登陆成功后，删除验证码信息并将用户信息存入redis中，用于验证当前登陆人员信息
		redisTemplate.delete(userDTO.getPhone());
		redisTemplate.opsForValue().set("user", user.getId(), 3, TimeUnit.DAYS);

		return R.success(user);
	}

}
