package com.itheima.reggie_take_out.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie_take_out.common.R;
import com.itheima.reggie_take_out.entity.Employee;
import com.itheima.reggie_take_out.enums.EmployeeEnum;
import com.itheima.reggie_take_out.enums.ExceptionEnum;
import com.itheima.reggie_take_out.enums.LoginEnum;
import com.itheima.reggie_take_out.exception.InternalException;
import com.itheima.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xushengjie
 * @create 2022/4/26 8:17 PM
 */

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private RedisTemplate redisTemplate;


	private static final String INIT_PASSWORD = "123456";


	/**
	 * 员工登陆接口
	 * @param request
	 * @param employee
	 * @return
	 */
	@PostMapping("/login")
	public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {


		//1.将页面提交的明文密码进行md5加密
		String password = employee.getPassword();
		password = SecureUtil.md5(password);

		//2.根据提交的用户名查询数据库
		LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(Employee::getUsername, employee.getUsername());
		//用户名是唯一的，使用getOne()方法获取
		Employee emp = employeeService.getOne(queryWrapper);
		if (emp == null) {
			return R.error(LoginEnum.USER_NOT_FOUND.getMsg());
		}


		//3.密码比对
		boolean equals = Objects.equals(emp.getPassword(), password);
		if (!equals) {
			return R.error(LoginEnum.PWD_INCORRECT.getMsg());
		}


		//4.查看员工的账号状态 0表示禁用，1表示正常
		if (emp.getStatus() == 0) {
			return R.error(LoginEnum.ACCOUNT_FORIDDEN.getMsg());
		}

		//5.登陆成功，将用户id存入redis中
		redisTemplate.opsForValue().set("employee", emp.getId(), 3, TimeUnit.DAYS);

		return R.success(emp);
	}


	/**
	 * 用户退出登录接口
	 * @param request
	 * @return
	 */
	@PostMapping("/logout")
	public R<String> logout(HttpServletRequest request) {
		//清楚session中保存的用户id
		request.getSession().removeAttribute("employee");
		return R.success("退出成功!");
	}


	/**
	 * 新增员工
	 * @param employee
	 * @return
	 */
	@PostMapping
	public R<String> saveEmployee(@RequestBody Employee employee) {
		if (employee == null) {
			return R.error(EmployeeEnum.USER_PARAM_INVALID.getMsg());
		}
		//密码进行md5加密
		employee.setPassword(SecureUtil.md5(INIT_PASSWORD));
		boolean save = employeeService.save(employee);
		if (!save) {
			return R.error(EmployeeEnum.SAVE_FAILED.getMsg());
		}

		return R.success(EmployeeEnum.SAVE_SUCCESS.getMsg());
	}



	/**
	 * 获取用户列表并分页；包含查询接口
	 * @param page
	 * @param pageSize
	 * @param name
	 * @return
	 */
	@GetMapping("/page")
	public R<Page<Employee>> page(Integer page, Integer pageSize, String name) {

		//创建分页构造器
		Page<Employee> objectPage = new Page<>(page, pageSize);

		//创建查询条件构造器
		LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.like(StringUtils.isNotBlank(name), Employee::getName, name);

		//根据更新时间降序排序
		queryWrapper.orderByDesc(Employee::getUpdateTime);

		//执行分页查询操作
		employeeService.page(objectPage, queryWrapper);

		return R.success(objectPage);
	}


	/**
	 * 修改用户信息，禁用启用账号操作
	 * @return
	 */
	@PutMapping
	public R<String> updateUserInfo(@RequestBody Employee employee) {

		if (employee == null) {
			throw new InternalException(ExceptionEnum.PARAM_IS_NULL);
		}

		boolean b = employeeService.updateById(employee);
		if (!b) {
			throw new InternalException(ExceptionEnum.SQL_EXEC_ERROR);
		}

		return R.success(EmployeeEnum.UPDATE_SUCCESS.getMsg());
	}


	/**
	 * 根据用户id获取用户信息
	 * @param id
	 * @return
	 */@GetMapping("/{id}")
	public R<Employee> getUserInfoById(@PathVariable Long id) {

	 	LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
	 	queryWrapper.eq(Employee::getId, id);
		Employee employeeServiceOne = employeeService.getOne(queryWrapper);
		if (employeeServiceOne == null) {
			throw new InternalException(ExceptionEnum.DATA_NOT_FOUND);
		}

		return R.success(employeeServiceOne);
	}
}
