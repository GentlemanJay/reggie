package com.itheima.reggie_take_out.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 * @author xushengjie
 */
@Data
@Accessors(chain = true)
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 用户名
     * 相当于唯一账号名，数据表中创建username为unique唯一索引
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    private String password;

    private String phone;

    private String sex;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 用户状态 0：禁用  1：正常
     */
    private Integer status;


    /**
     * fill表示自动填充字段，在新增更新时自动填充公共字段
     * MyMetaObjectHandler配置类实现
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    /**
     * 修改人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
