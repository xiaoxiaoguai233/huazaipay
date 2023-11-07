package org.xxpay.core.common.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Setter
@Getter
public class MerchantPayOrderVO {
    /**
     * 支付订单号
     *
     * @mbggenerated
     */
    private String payOrderId;

    /**
     * 商户ID
     *
     * @mbggenerated
     */
    private Long mchId;

    /**
     * 商户费率
     *
     * @mbggenerated
     */
    private BigDecimal mchRate;

    /**
     * 商户入账,单位分
     *
     * @mbggenerated
     */
    private Long mchIncome;

    /**
     * 应用ID
     *
     * @mbggenerated
     */
    private String appId;

    /**
     * 商户订单号
     *
     * @mbggenerated
     */
    private String mchOrderNo;


    /**
     * 支付产品ID
     *
     * @mbggenerated
     */
    private Integer productId;


    /**
     * 支付金额,单位分
     *
     * @mbggenerated
     */
    private Long amount;


    /**
     * 支付状态,0-订单生成,1-支付中,2-支付成功,3-业务处理完成,4-已退款
     *
     * @mbggenerated
     */
    private Byte status;


    /**
     * 商品标题
     *
     * @mbggenerated
     */
    private String subject;

    /**
     * 商品描述信息
     *
     * @mbggenerated
     */
    private String body;

    /**
     * 特定渠道发起额外参数
     *
     * @mbggenerated
     */
    private String extra;

    /**
     * 渠道用户标识,如微信openId,支付宝账号
     *
     * @mbggenerated
     */
    private String channelUser;


    /**
     * 渠道订单号
     *
     * @mbggenerated
     */
    private String channelOrderNo;

    /**
     * 渠道数据包
     *
     * @mbggenerated
     */
    private String channelAttach;

    /**
     * 平台利润,单位分
     *
     * @mbggenerated
     */
    private Long platProfit;


    /**
     * 是否退款,0-未退款,1-退款
     *
     * @mbggenerated
     */
    private Byte isRefund;

    /**
     * 退款次数
     *
     * @mbggenerated
     */
    private Integer refundTimes;

    /**
     * 成功退款金额,单位分
     *
     * @mbggenerated
     */
    private Long successRefundAmount;

    /**
     * 扩展参数1
     *
     * @mbggenerated
     */
    private String param1;

    /**
     * 扩展参数2
     *
     * @mbggenerated
     */
    private String param2;

    /**
     * 通知地址
     *
     * @mbggenerated
     */
    private String notifyUrl;

    /**
     * 跳转地址
     *
     * @mbggenerated
     */
    private String returnUrl;

    /**
     * 订单失效时间
     *
     * @mbggenerated
     */
    private Date expireTime;

    /**
     * 订单支付成功时间
     *
     * @mbggenerated
     */
    private Date paySuccTime;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 更新时间
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * 产品类型:1-收款,2-充值
     *
     * @mbggenerated
     */
    private Byte productType;

    /**
     * 商户子账户
     *
     * @mbggenerated
     */
    private Long subMchId;
}

//payOrderId,mchId,mchRate,mchIncome,appId,mchOrderNo,productId,amount,status,subject,body,extra,channelUser,channelOrderNo,channelAttach,platProfit,isRefund,refundTimes,successRefundAmount,param1,param2,notifyUrl,returnUrl,expireTime,paySuccTime,createTime,updateTime,productType,subMchId,



//"支付订单号,商户ID,商户费率,商户入账,应用ID,商户订单号,支付产品ID,支付金额,支付状态,商品标题,商品描述信息,特定渠道发起额外参数,渠道用户标识,渠道订单号,渠道数据包,平台利润,是否退款,退款次数,成功退款金额,扩展参数1,扩展参数2,通知地址,跳转地址,订单失效时间,订单支付成功时间,创建时间,更新时间,产品类型,商户子账户"