package com.itheima.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie_take_out.entity.Category;
import com.itheima.reggie_take_out.mapper.CategoryMapper;
import com.itheima.reggie_take_out.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xushengjie
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
implements CategoryService{
}
