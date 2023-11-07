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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_z_hsh_account")
public class ZHshAccount implements Serializable {

    private static final long serialVersionUID=1L;

    public static final LambdaQueryWrapper<Z7881Account> gw(){
        return new LambdaQueryWrapper<>();
    }

    /**
     * id
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Long id;

    /**
     * 被谁创建
     */
    @TableField("ParentId")
    private Long parentId;

    /**
     * token
     */
    @TableField("Token")
    private String token;

    /**
     * 手机号码
     */
    @TableField("Phone")
    private String phone;

    /**
     * 今已收金
     */
    @TableField("TodayMoney")
    private Long todayMoney;

    /**
     * 限制日收金
     */
    @TableField("LimitDayMoney")
    private Long limitDayMoney;

    /**
     * 总收金
     */
    @TableField("TotalMoney")
    private Long totalMoney;

    /**
     * 限制总收金
     */
    @TableField("LimitMaxMoney")
    private Long limitMaxMoney;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("UpdateTime")
    private Date updateTime;

    /**
     * 备注
     */
    @TableField("Remark")
    private String remark;

    /**
     * 状态 1-正常，2-禁用，3-限额
     */
    @TableField("State")
    private Byte state;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
