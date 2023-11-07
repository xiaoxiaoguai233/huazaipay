package org.xxpay.core.common.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Data
@Getter
@Setter
public class ManagerSettOrderVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * ID
     *
     * @mbggenerated
     */
//    private Long id;

    /**
     * 代理商或商户ID
     *
     * @mbggenerated
     */
    private Long infoId;

    /**
     * 结算商类型:1-代理商,2-商户
     *
     * @mbggenerated
     */
    private String infoType;

    /**
     * 结算发起类型:1-手工结算,2-自动结算
     *
     * @mbggenerated
     */
//    private String settType;

    /**
     * 结算日期
     *
     * @mbggenerated
     */
    private String settDate;

    /**
     * 申请结算金额
     *
     * @mbggenerated
     */
    private Long settAmount;

    /**
     * 结算手续费
     *
     * @mbggenerated
     */
    private Long settFee;

    /**
     * 结算打款金额
     *
     * @mbggenerated
     */
    private Long remitAmount;

    /**
     * 账户属性:0-对私,1-对公,默认对私
     *
     * @mbggenerated
     */
//    private Byte accountAttr;

    /**
     * 账户类型:1-银行卡转账,2-微信转账,3-支付宝转账
     *
     * @mbggenerated
     */
//    private Byte accountType;

    /**
     * 开户行名称
     *
     * @mbggenerated
     */
//    private String bankName;

    /**
     * 开户网点名称
     *
     * @mbggenerated
     */
//    private String bankNetName;

    /**
     * 账户名
     *
     * @mbggenerated
     */
    private String accountName;

    /**
     * 账户号
     *
     * @mbggenerated
     */
    private String accountNo;

    /**
     * 开户行所在省
     *
     * @mbggenerated
     */
//    private String province;

    /**
     * 开户行所在市
     *
     * @mbggenerated
     */
//    private String city;

    /**
     * 结算状态:1-等待审核,2-已审核,3-审核不通过,4-打款中,5-打款成功,6-打款失败
     *
     * @mbggenerated
     */
    private String settStatus;

    /**
     * 备注
     *
     * @mbggenerated
     */
//    private String remark;

    /**
     * 打款备注
     *
     * @mbggenerated
     */
//    private String remitRemark;

    /**
     * 操作用户ID
     *
     * @mbggenerated
     */
//    private Long operatorId;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
//    private String createTime;

    /**
     * 更新时间
     *
     * @mbggenerated
     */
//    private Date updateTime;

    /**
     * 结算单号
     *
     * @mbggenerated
     */
    private String settOrderId;

    /**
     * 转账订单号
     *
     * @mbggenerated
     */
//    private String transOrderId;

    /**
     * 转账返回消息
     *
     * @mbggenerated
     */
//    private String transMsg;
}
//"ID,代理商或商户ID,结算商类型,结算发起类型,结算日期,申请结算金额,结算手续费,结算打款金额,账户属性,账户类型,开户行名称,开户网点名称,账户名,账户号,开户行所在省,开户行所在市,结算状态,备注,打款备注,操作用户ID,创建时间,更新时间,结算单号,转账订单号,转账返回消息"