package com.itheima.reggie_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie_take_out.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author xushengjie
 * @Entity com.itheima.reggie_take_out.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
