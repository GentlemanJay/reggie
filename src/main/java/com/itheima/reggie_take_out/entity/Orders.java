package com.itheima.reggie_take_out.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 * @author xushengjie
 * @TableName orders
 */
@TableName(value ="orders")
@Data
@Accessors(chain = true)
public class Orders implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 订单号
     */
    private String number;

    /**
     * 订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
     */
    private Integer status;

    /**
     * 下单用户
     */
    private Long userId;

    /**
     * 地址id
     */
    private Long addressBookId;

    /**
     * 下单时间
     */
    private LocalDateTime orderTime;

    /**
     * 结账时间
     */
    private LocalDateTime checkoutTime;

    /**
     * 支付方式 1微信,2支付宝
     */
    private Integer payMethod;

    /**
     * 实收金额
     */
    private BigDecimal amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 
     */
    private String phone;

    /**
     * 
     */
    private String address;

    /**
     * 
     */
    private String userName;

    /**
     * 联系人
     */
    private String consignee;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}