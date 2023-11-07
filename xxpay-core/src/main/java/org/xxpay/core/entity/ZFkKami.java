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
@TableName("t_z_fk_kami")
public class ZFkKami implements Serializable {

    private static final long serialVersionUID=1L;

    public static final LambdaQueryWrapper<ZFkKami> gw(){
        return new LambdaQueryWrapper<>();
    }

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建人ID（小于300000的都是管理员创建，大于都是码商）
     */
    @TableField(value = "user_id")
    private Long user_id;

    /**
     * 创建人姓名
     */
    @TableField("user_name")
    private String user_name;

    /**
     * 发放人id
     */
    @TableField(value = "provide_user_id")
    private Long provide_user_id;

    /**
     * 发放人名称
     */
    @TableField("provide_user_name")
    private String provide_user_name;

    /**
     * 卡密金额
     */
    @TableField(value = "amount")
    private Long amount;

    /**
     * 订单id
     */
    @TableField(value = "order_id")
    private String order_id;

    /**
     * 卡号
     */
    @TableField("card")
    private String card;

    /**
     * 卡密
     */
    @TableField("card_pwd")
    private String card_pwd;


    /**
     * 添加时间
     */
    @TableField("add_time")
    private Date add_time;

    /**
     * 购买时间
     */
    @TableField("buy_time")
    private Date buy_time;

    /**
     * 使用时间
     */
    @TableField("use_time")
    private Date use_time;

    /**
     * 0可用 ,1已发放，2 已使用
     */
    @TableField("state")
    private String state;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
