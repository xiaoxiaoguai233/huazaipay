package org.xxpay.core.service;

import org.xxpay.core.entity.AssistantAccount;

import java.math.BigDecimal;
import java.util.List;

public interface IAssistantAccountService {

    List<AssistantAccount> listAll(int offset, int limit);

    AssistantAccount findByAssistantId(Long AssistantId);

    int updateSettAmount(long AssistantId, long totalAmount);

    /**
     * 给代理商户加款
     * @param AssistantId
     * @param amount
     * @param bizType
     */
    void creditToAccount(Long AssistantId, Long amount, Byte bizType);

    /**
     * 给代理商户加款
     * @param AssistantId
     * @param amount
     * @param bizType
     */
    void creditToAccount(Long AssistantId, Long amount, Byte bizType, String bizItem, String orderId);

    /**
     * 给代理商账户减款款
     * @param AssistantId
     * @param amount
     * @param bizType
     */
    void debitToAccount(Long AssistantId, Long amount, Byte bizType);

    /**
     * 给代理商账户减款款
     * @param AssistantId
     * @param amount
     * @param bizType
     */
    void debitToAccount(Long AssistantId, Long amount, Byte bizType, String bizItem);

    /**
     * 给代理商账户冻结金额(增加用户不可用金额)
     * @param AssistantId
     * @param freezeAmount
     */
    void freezeAmount(Long AssistantId, Long freezeAmount);

    /**
     * 结算成功给代理商解冻金额并减款(减少账户余额,减少账户不可用金额,减少账户可结算金额)
     * @param AssistantId
     * @param amount
     * @param requestNo
     * @param bizType
     */
    void unFreezeAmount(Long AssistantId, Long amount, String requestNo, Byte bizType);

    /** 结算失败：解冻 **/
    /**
     * 结算失败给代理商账户解冻金额(减少账户不可用金额)
     * @param AssistantId
     * @param amount
     */
    void unFreezeSettAmount(Long AssistantId, Long amount);

    /**
     * 给代理商账户加钱
     * @param AssistantId
     * @param amount
     */
    void credit2Account(Long AssistantId, Byte bizType, Long amount);

    /**
     * 给代理商账户加钱
     * @param AssistantId
     * @param amount
     */
    void credit2Account(Long AssistantId, Byte bizType, Long amount, String bizItem);

    /**
     * 给代理商账户减钱
     * @param AssistantId
     * @param amount
     */
    void debit2Account(Long AssistantId, Byte bizType, Long amount);

    /**
     * 给代理商账户减钱
     * @param AssistantId
     * @param amount
     */
    void debit2Account(Long AssistantId, Byte bizType, Long amount, String bizItem);


    /**
     * 给代理商账户减钱
     * @param AssistantId
     * @param amount
     */
    void debit2Account(Long AssistantId, Byte bizType, Long amount, String bizItem, String card);

    public BigDecimal sumAssistantBalance(AssistantAccount record);

}
