package org.xxpay.core.common.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Setter
@Getter
public class ManagerPayOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;
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
     * 商户订单号
     *
     * @mbggenerated
     */
    private String mchOrderNo;

    /**
     * 代理商ID
     *
     * @mbggenerated
     */
//    private Long agentId;

    /**
     * 一级代理商ID
     *
     * @mbggenerated
     */
//    private Long parentAgentId;

    /**
     * 代理商费率
     *
     * @mbggenerated
     */
//    private BigDecimal agentRate;

    /**
     * 一级代理商费率
     *
     * @mbggenerated
     */
//    private BigDecimal parentAgentRate;

    /**
     * 代理商利润,单位分
     *
     * @mbggenerated
     */
//    private Long agentProfit;

    /**
     * 一级代理商利润,单位分
     *
     * @mbggenerated
     */
//    private Long parentAgentProfit;

    /**
     * 支付产品ID
     *
     * @mbggenerated
     */
    private String productId;

    /**
     * 通道ID
     *
     * @mbggenerated
     */
//    private Integer passageId;

    /**
     * 通道账户ID
     *
     * @mbggenerated
     */
//    private Integer passageAccountId;

    /**
     * 渠道类型,对接支付接口类型代码
     *
     * @mbggenerated
     */
    private String channelType;

    /**
     * 渠道ID,对应支付接口代码
     *
     * @mbggenerated
     */
    private String channelId;

    /**
     * 支付金额,单位分
     *
     * @mbggenerated
     */
    private String amount;

    /**
     * 三位货币代码,人民币:cny
     *
     * @mbggenerated
     */
//    private String currency;

    /**
     * 支付状态,0-订单生成,1-支付中,2-支付成功,3-业务处理完成,4-已退款
     *
     * @mbggenerated
     */
    private String status;

    /**
     * 客户端IP
     *
     * @mbggenerated
     */
//    private String clientIp;

    /**
     * 设备
     *
     * @mbggenerated
     */
//    private String device;

    /**
     * 商品标题
     *
     * @mbggenerated
     */
//    private String subject;

    /**
     * 商品描述信息
     *
     * @mbggenerated
     */
//    private String body;

    /**
     * 特定渠道发起额外参数
     *
     * @mbggenerated
     */
//    private String extra;

    /**
     * 渠道用户标识,如微信openId,支付宝账号
     *
     * @mbggenerated
     */
//    private String channelUser;

    /**
     * 渠道商户ID
     *
     * @mbggenerated
     */
//    private String channelMchId;

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
//    private String channelAttach;

    /**
     * 平台利润,单位分
     *
     * @mbggenerated
     */
//    private Long platProfit;

    /**
     * 渠道费率
     *
     * @mbggenerated
     */
//    private BigDecimal channelRate;

    /**
     * 渠道成本,单位分
     *
     * @mbggenerated
     */
//    private Long channelCost;

    /**
     * 是否退款,0-未退款,1-退款
     *
     * @mbggenerated
     */
//    private Byte isRefund;

    /**
     * 退款次数
     *
     * @mbggenerated
     */
//    private Integer refundTimes;

    /**
     * 成功退款金额,单位分
     *
     * @mbggenerated
     */
//    private Long successRefundAmount;

    /**
     * 渠道支付错误码
     *
     * @mbggenerated
     */
//    private String errCode;

    /**
     * 渠道支付错误描述
     *
     * @mbggenerated
     */
//    private String errMsg;

    /**
     * 扩展参数1
     *
     * @mbggenerated
     */
//    private String param1;

    /**
     * 扩展参数2
     *
     * @mbggenerated
     */
//    private String param2;

    /**
     * 通知地址
     *
     * @mbggenerated
     */
//    private String notifyUrl;

    /**
     * 跳转地址
     *
     * @mbggenerated
     */
//    private String returnUrl;

    /**
     * 订单失效时间
     *
     * @mbggenerated
     */
//    private Date expireTime;

    /**
     * 订单支付成功时间
     *
     * @mbggenerated
     */
//    private Date paySuccTime;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private String createTime;

    /**
     * 更新时间
     *
     * @mbggenerated
     */
//    private Date updateTime;

    /**
     * 产品类型:1-收款,2-充值
     *
     * @mbggenerated
     */
    private String productType;

    /**
     * 商户子账户
     *
     * @mbggenerated
     */
    private String subMchId;


    public String getPayOrderId() {
        return payOrderId;
    }

    public void setPayOrderId(String payOrderId) {
        this.payOrderId = payOrderId;
    }

    public Long getMchId() {
        return mchId;
    }

    public void setMchId(Long mchId) {
        this.mchId = mchId;
    }

    public String getMchOrderNo() {
        return mchOrderNo;
    }

    public void setMchOrderNo(String mchOrderNo) {
        this.mchOrderNo = mchOrderNo;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getSubMchId() {
        return subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
    }
}
//"支付订单号,商户ID,商户类型,商户费率,商户入账,应用ID,商户订单号,代理商ID,一级代理商ID,代理商费率,一级代理商费率,代理商利润,一级代理商利润,支付产品ID,通道ID,通道账户ID,渠道类型,渠道ID,支付金额,三位货币代码,支付状态,客户端IP,设备,商品标题,商品描述信息,特定渠道发起额外参数,渠道用户标识,渠道商户ID,渠道订单号,渠道数据包,平台利润,渠道费率,渠道成本,是否退款,退款次数,成功退款金额,渠道支付错误码,渠道支付错误描述,扩展参数1,扩展参数2,通知地址,跳转地址,订单失效时间,订单支付成功时间,创建时间,更新时间,产品类型,商户子账户"
