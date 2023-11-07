package org.xxpay.core.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 7881账号
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_z_7881_account")
public class Z7881Account implements Serializable {

    private static final long serialVersionUID=1L;

    public static final LambdaQueryWrapper<Z7881Account> gw(){
        return new LambdaQueryWrapper<>();
    }

    /**
     * id
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Long Id;

    /**
     * 被谁创建
     */
    @TableField("ParentId")
    private Long ParentId;

    /**
     * token
     */
    @TableField("Token")
    private String Token;

    /**
     * 手机号码
     */
    @TableField("Phone")
    private String Phone;

    /**
     * 今已收金
     */
    @TableField("TodayMoney")
    private Long TodayMoney;

    /**
     * 限制日收金
     */
    @TableField("LimitDayMoney")
    private Long LimitDayMoney;

    /**
     * 总收金
     */
    @TableField("TotalMoney")
    private Long TotalMoney;

    /**
     * 限制总收金
     */
    @TableField("LimitMaxMoney")
    private Long LimitMaxMoney;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private Date CreateTime;

    /**
     * 更新时间
     */
    @TableField("UpdateTime")
    private Date UpdateTime;

    /**
     * 备注
     */
    @TableField("Remark")
    private String Remark;

    /**
     * 状态 1-正常，2-禁用，3-限额
     */
    @TableField("State")
    private Byte State;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }


}
