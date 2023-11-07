package org.xxpay.core.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_z_hxt_account")
public class ZHxtAccount implements Serializable {

    private static final long serialVersionUID=1L;

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
     * 商店ID
     */
    @TableField("StoreId")
    private String storeId;

    /**
     * 商店ID
     */
    @TableField("NumberCard")
    private String numberCard;

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
     * 状态 1-正常，2-禁用，3-限额
     */
    @TableField("AddressId")
    private Long addressId;
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
