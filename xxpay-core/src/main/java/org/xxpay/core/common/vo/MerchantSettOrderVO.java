package org.xxpay.core.common.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class MerchantSettOrderVO {


    /**
     * 结算发起类型:1-手工结算,2-自动结算
     *
     * @mbggenerated
     */
    private Byte settType;

    /**
     * 结算日期
     *
     * @mbggenerated
     */
    private Date settDate;

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
    private Byte accountAttr;

    /**
     * 账户类型:1-银行卡转账,2-微信转账,3-支付宝转账
     *
     * @mbggenerated
     */
    private Byte accountType;

    /**
     * 开户行名称
     *
     * @mbggenerated
     */
    private String bankName;

    /**
     * 开户网点名称
     *
     * @mbggenerated
     */
    private String bankNetName;

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
    private String province;

    /**
     * 开户行所在市
     *
     * @mbggenerated
     */
    private String city;

    /**
     * 结算状态:1-等待审核,2-已审核,3-审核不通过,4-打款中,5-打款成功,6-打款失败
     *
     * @mbggenerated
     */
    private Byte settStatus;

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
    private String transOrderId;

}


//settType,settDate,settAmount,settFee,remitAmount,accountAttr,accountType,bankName,bankNetName,accountName,accountNo,province,city,settStatus,settOrderId,transOrderId


//"结算发起类型,结算日期,申请结算金额,结算手续费,结算打款金额,账户属性,账户类型,开户行名称,开户网点名称,账户名,账户号,开户行所在省,开户行所在市,结算状态,结算单号,转账订单号"
