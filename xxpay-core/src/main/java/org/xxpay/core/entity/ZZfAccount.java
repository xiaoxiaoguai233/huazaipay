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
@TableName("t_z_zf_account")
public class ZZfAccount implements Serializable {

    private static final long serialVersionUID = 1L;

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


    private String parentName;

    /**
     * 卡密类型（1-京东E卡，2-其他卡）
     */
    @TableField("TypeId")
    private Long typeId;

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
     * app_key
     */
    @TableField("AppKey")
    private String appKey;

    /**
     * AppSecret
     */
    @TableField("AppSecret")
    private String appSecret;

    /**
     * Address
     */
    @TableField("Address")
    private String address;

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


    /**
     * 商店ID
     */
    @TableField("StoreId")
    private String storeId;


    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
