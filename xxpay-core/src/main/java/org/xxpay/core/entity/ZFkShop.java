package org.xxpay.core.entity;

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
@TableName("t_z_fk_kami_shop")
public class ZFkShop implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "Id", type = IdType.AUTO)
    private Long id;

    /**
     * name
     */
    @TableField("Name")
    private String name;

    /**
     * Link
     */
    @TableField("Link")
    private String link;

    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private Date createTime;

    /**
     * 备注
     */
    @TableField("Remark")
    private String remark;

    /**
     * 状态 1-正常，2-禁用
     */
    @TableField("State")
    private Byte state;

}
