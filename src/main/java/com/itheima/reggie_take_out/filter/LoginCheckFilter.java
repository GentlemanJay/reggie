package com.itheima.reggie_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie_take_out.common.BaseContext;
import com.itheima.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登陆
 * @author xushengjie
 * @create 2022/4/29 3:00 PM
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 路径匹配器，支持通配符
	 */
	public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest)servletRequest;
		HttpServletResponse response = (HttpServletResponse)servletResponse;

		//1、获取本次请求的URI
		String requestURI = request.getRequestURI();

		//定义不需要处理的请求路径
		String[] urls = new String[]{
				"/employee/login",
				"/employee/logout",
				"/backend/**",
				"/front/**",
				"/user/login",
				"/user/sendMsg"
		};


		//2、判断本次请求是否需要处理
		boolean check = check(urls, requestURI);

		//3、如果不需要处理，则直接放行
		if(check){
			filterChain.doFilter(request,response);
			return;
		}

		//4-1、判断后台管理员工登录状态，如果已登录，则直接放行
		Long empId = (Long) redisTemplate.opsForValue().get("employee");
		if( empId != null){

			//用户成功登陆后，将当前用户id设置到当前ThreadLocal中
			BaseContext.setCurrentId(empId);

			filterChain.doFilter(request,response);
			return;
		}


		//4-2、判断前台用户登录状态，如果已登录，则直接放行
		Long userId = (Long) redisTemplate.opsForValue().get("user");
		if( userId != null){

			//用户成功登陆后，将当前用户id设置到当前ThreadLocal中
			BaseContext.setCurrentId(userId);

			filterChain.doFilter(request,response);
			return;
		}


		//5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
		response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

	}



	/**
	 * 路径匹配，检查本次请求是否需要放行
	 * @param urls
	 * @param requestURI
	 * @return
	 */
	public boolean check(String[] urls,String requestURI){
		for (String url : urls) {
			boolean match = PATH_MATCHER.match(url, requestURI);
			if(match){
				return true;
			}
		}
		return false;
	}
}
