package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.util.DateUtils;
import org.xxpay.core.common.util.MySeq;
import org.xxpay.core.entity.*;
import org.xxpay.core.service.IAssistantAccountService;
import org.xxpay.service.dao.mapper.AssistantAccountHistoryMapper;
import org.xxpay.service.dao.mapper.AssistantAccountMapper;
import org.xxpay.service.dao.mapper.AssistantAccountMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service(interfaceName = "org.xxpay.core.service.IAssistantAccountService", version = "1.0.0", retries = -1)
public class AssistantAccountServiceImpl implements IAssistantAccountService {

    @Autowired
    private AssistantAccountMapper assistantAccountMapper;

    @Autowired
    private AssistantAccountHistoryMapper assistantAccountHistoryMapper;

    @Override
    public List<AssistantAccount> listAll(int offset, int limit) {
        AssistantAccountExample example = new AssistantAccountExample();
        example.setOrderByClause("createTime asc");
        example.setLimit(limit);
        example.setOffset(offset);
        return assistantAccountMapper.selectByExample(example);
    }


    @Override
    public AssistantAccount findByAssistantId(Long assistantId) {
        return assistantAccountMapper.selectByPrimaryKey(assistantId);
    }

    /**
     * 更新代理商可结算金额(更新后可结算金额=已有可结算金额+totalAmount)
     * @param assistantId
     * @param totalAmount
     * @return
     */
    @Override
    public int updateSettAmount(long assistantId, long totalAmount) {
        Map param = new HashMap<>();
        param.put("assistantId", assistantId);
        param.put("totalAmount", totalAmount);
        return assistantAccountMapper.updateSettAmount(param);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void creditToAccount(Long assistantId, Long amount, Byte bizType) {
        creditToAccount(assistantId, amount, bizType, "", null);

    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void creditToAccount(Long assistantId, Long amount, Byte bizType, String bizItem, String orderId) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);

        AssistantAccount updateAssistantAccount = new AssistantAccount();
        // 更新今日进出账,不为同一天,直接清零
        Date lastAccountUpdateDate = assistantAccount.getAccountUpdateTime(); // 账户修改时间
        if(!DateUtils.isSameDayWithToday(lastAccountUpdateDate)) {
            updateAssistantAccount.setTodayExpend(0l);
            updateAssistantAccount.setTodayIncome(0l);
        }

        // 总收益累加和今日收益
        if (MchConstant.AGENT_BIZ_TYPE_PROFIT == bizType) {// 业务类型是分润
            updateAssistantAccount.setTotalIncome(assistantAccount.getTotalIncome() + amount);

            /***** 根据上次修改时间，统计今日收益 *******/
            if (DateUtils.isSameDayWithToday(lastAccountUpdateDate)) {
                // 如果是同一天
                updateAssistantAccount.setTodayIncome(updateAssistantAccount.getTodayIncome() + amount);
            } else {
                // 不是同一天
                updateAssistantAccount.setTodayIncome(amount);
            }
            /************************************/
        }

        // 记录资金流水
        AssistantAccountHistory assistantAccountHistory = new AssistantAccountHistory();
        assistantAccountHistory.setAssistantId(assistantId);
        assistantAccountHistory.setAmount(amount);
        assistantAccountHistory.setBalance(assistantAccount.getBalance());
        assistantAccountHistory.setAfterBalance(assistantAccount.getBalance() + amount);
        assistantAccountHistory.setBizType(bizType);
        assistantAccountHistory.setBizItem(bizItem);
        assistantAccountHistory.setFundDirection(MchConstant.FUND_DIRECTION_ADD);
        assistantAccountHistory.setOrderId(orderId);

        // 账户余额
        updateAssistantAccount.setBalance(assistantAccount.getBalance() + amount);
        // 账户可结算金额
        updateAssistantAccount.setSettAmount(assistantAccount.getSettAmount() + amount);
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andBalanceEqualTo(assistantAccount.getBalance());
        criteria.andSettAmountEqualTo(assistantAccount.getSettAmount());

        // 数据操作,保证事务
        updateAccountAmount4Transactional(assistantAccountHistory, updateAssistantAccount, example);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void debitToAccount(Long assistantId, Long amount, Byte bizType) {
        debitToAccount(assistantId, amount, bizType, "");
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void debitToAccount(Long assistantId, Long amount, Byte bizType, String bizItem) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);

        Long availableBalance = assistantAccount.getAvailableBalance();
        if(availableBalance.compareTo(amount) == -1) {
            // 余额不足减款
            throw ServiceException.build(RetEnum.RET_SERVICE_ACCOUNT_BALANCE_OUT_LIMIT);
        }

        AssistantAccount updateAssistantAccount = new AssistantAccount();
        // 更新今日进出账,不为同一天,直接清零
        Date lastUpdateDate = assistantAccount.getAccountUpdateTime(); // 应该修改为账户修改时间
        if(!DateUtils.isSameDayWithToday(lastUpdateDate)) {
            updateAssistantAccount.setTodayExpend(amount);
            updateAssistantAccount.setTodayIncome(0l);
        }else {
            updateAssistantAccount.setTodayExpend(assistantAccount.getTodayExpend() + amount);
        }
        updateAssistantAccount.setTotalExpend(assistantAccount.getTotalExpend() + amount);
        // 更新后账户余额
        updateAssistantAccount.setBalance(assistantAccount.getBalance() - amount);

        // 记录资金流水
        AssistantAccountHistory assistantAccountHistory = new AssistantAccountHistory();
        assistantAccountHistory.setAssistantId(assistantId);
        assistantAccountHistory.setAmount(amount);
        assistantAccountHistory.setBalance(assistantAccount.getBalance());
        assistantAccountHistory.setAfterBalance(assistantAccount.getBalance() - amount);
        assistantAccountHistory.setBizType(bizType);
        assistantAccountHistory.setBizItem(bizItem);
        assistantAccountHistory.setFundDirection(MchConstant.FUND_DIRECTION_SUB);

        // 账户余额
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andBalanceEqualTo(assistantAccount.getBalance());

        // 数据操作,保证事务
        updateAccountAmount4Transactional(assistantAccountHistory, updateAssistantAccount, example);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void freezeAmount(Long assistantId, Long freezeAmount) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);
        // 验证可用余额是否足够
        if(!assistantAccount.availableBalanceIsEnough(freezeAmount)) {
            // 冻结金额超限
            throw ServiceException.build(RetEnum.RET_SERVICE_FREEZE_AMOUNT_OUT_LIMIT);
        }

        // 更新冻结金额时,保证更新时数据一致,使用update锁
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        Long oldUnBalance = assistantAccount.getUnBalance();
        BigDecimal newUnBalance = new BigDecimal(oldUnBalance).add(new BigDecimal(freezeAmount));
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andUnBalanceEqualTo(oldUnBalance);

        AssistantAccount updateAssistantAccount = new AssistantAccount();
        updateAssistantAccount.setUnBalance(newUnBalance.longValue());
        int updateCount = assistantAccountMapper.updateByExampleSelective(updateAssistantAccount, example);
        if(updateCount != 1) {
            // 更新失败,抛出异常
            throw ServiceException.build(RetEnum.RET_SERVICE_ACCOUNT_FROZEN_AMOUNT_FAIL);
        }
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void unFreezeAmount(Long assistantId, Long amount, String requestNo, Byte bizType) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);

        AssistantAccount updateAssistantAccount = new AssistantAccount();
        // 判断解冻金额是否超限
        if(assistantAccount.getUnBalance() < amount) {
            throw ServiceException.build(RetEnum.RET_SERVICE_UN_FREEZE_AMOUNT_OUT_LIMIT);
        }

        // 记录资金流水
        AssistantAccountHistory assistantAccountHistory = new AssistantAccountHistory();
        assistantAccountHistory.setAssistantId(assistantId);
        assistantAccountHistory.setAmount(amount);
        assistantAccountHistory.setBalance(assistantAccount.getBalance());
        assistantAccountHistory.setAfterBalance(assistantAccount.getBalance() - amount);
        assistantAccountHistory.setBizType(bizType);
        assistantAccountHistory.setFundDirection(MchConstant.FUND_DIRECTION_SUB);

        // 更新账户
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andUnBalanceEqualTo(assistantAccount.getUnBalance());
        criteria.andBalanceEqualTo(assistantAccount.getBalance());
        criteria.andSettAmountEqualTo(assistantAccount.getSettAmount());
        // 更新今日进出账,不为同一天,直接清零
        Date lastUpdateDate = assistantAccount.getAccountUpdateTime(); // 账户修改时间
        if(!DateUtils.isSameDayWithToday(lastUpdateDate)) {
            assistantAccount.setTodayExpend(amount);
            assistantAccount.setTodayIncome(0l);
        }else {
            assistantAccount.setTodayExpend(assistantAccount.getTodayExpend() + amount);
        }
        updateAssistantAccount.setBalance(assistantAccount.getBalance() - amount);          // 减款
        updateAssistantAccount.setUnBalance(assistantAccount.getUnBalance() - amount);      // 解冻
        updateAssistantAccount.setSettAmount(assistantAccount.getSettAmount() - amount);    // 减少可结算金额

        // 数据操作,保证事务
        updateAccountAmount4Transactional(assistantAccountHistory, updateAssistantAccount, example);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void unFreezeSettAmount(Long assistantId, Long amount) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);
        // 判断解冻金额是否超限
        if(assistantAccount.getUnBalance() < amount) {
            throw ServiceException.build(RetEnum.RET_SERVICE_UN_FREEZE_AMOUNT_OUT_LIMIT);
        }
        AssistantAccount updateAssistantAccount = new AssistantAccount();
        // 更新今日进出账,不为同一天,直接清零
        Date lastUpdateDate = assistantAccount.getAccountUpdateTime(); // 账户修改时间
        if(!DateUtils.isSameDayWithToday(lastUpdateDate)) {
            updateAssistantAccount.setTodayExpend(0l);
            updateAssistantAccount.setTodayIncome(0l);
        }
        // 更新账户不可用金额
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andUnBalanceEqualTo(assistantAccount.getUnBalance());
        updateAssistantAccount.setUnBalance(assistantAccount.getUnBalance() - amount);
        int updateCount = assistantAccountMapper.updateByExampleSelective(updateAssistantAccount, example);
        if(updateCount != 1) {
            // 更新失败,抛出异常
            throw ServiceException.build(RetEnum.RET_SERVICE_ACCOUNT_AMOUNT_UPDATE_FAIL);
        }
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void credit2Account(Long assistantId, Byte bizType, Long amount) {
        credit2Account(assistantId, bizType, amount, "");
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void credit2Account(Long assistantId, Byte bizType, Long amount, String bizItem) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);

        AssistantAccount updateAssistantAccount = new AssistantAccount();
        // 记录资金流水
        AssistantAccountHistory assistantAccountHistory = new AssistantAccountHistory();
        assistantAccountHistory.setAssistantId(assistantId);
        assistantAccountHistory.setAmount(amount);
        assistantAccountHistory.setBalance(assistantAccount.getBalance());
        assistantAccountHistory.setAfterBalance(assistantAccount.getBalance() + amount);
        assistantAccountHistory.setBizType(bizType);
        assistantAccountHistory.setBizItem(bizItem);
        assistantAccountHistory.setOrderId(MySeq.getChangeAccount());
        assistantAccountHistory.setFundDirection(MchConstant.FUND_DIRECTION_ADD);

        // 账户余额
        updateAssistantAccount.setBalance(assistantAccount.getBalance() + amount);
        updateAssistantAccount.setSettAmount(assistantAccount.getSettAmount() + amount);
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andBalanceEqualTo(assistantAccount.getBalance());
        criteria.andSettAmountEqualTo(assistantAccount.getSettAmount());

        // 数据操作,保证事务
        updateAccountAmount4Transactional(assistantAccountHistory, updateAssistantAccount, example);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void debit2Account(Long assistantId, Byte bizType, Long amount) {
        debit2Account(assistantId, bizType, amount, "");
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void debit2Account(Long assistantId, Byte bizType, Long amount, String bizItem) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);

        Long availableBalance = assistantAccount.getAvailableBalance();
        if(availableBalance.compareTo(amount) == -1) {
            // 余额不足减款
            throw ServiceException.build(RetEnum.RET_SERVICE_ACCOUNT_BALANCE_OUT_LIMIT);
        }

        AssistantAccount updateAssistantAccount = new AssistantAccount();

        // 更新后账户余额
        updateAssistantAccount.setBalance(assistantAccount.getBalance() - amount);
        // 更新后可结算金额
        updateAssistantAccount.setSettAmount(assistantAccount.getSettAmount() - amount);

        // 记录资金流水
        AssistantAccountHistory assistantAccountHistory = new AssistantAccountHistory();
        assistantAccountHistory.setAssistantId(assistantId);
        assistantAccountHistory.setAmount(amount);
        assistantAccountHistory.setBalance(assistantAccount.getBalance());
        assistantAccountHistory.setAfterBalance(assistantAccount.getBalance() - amount);
        assistantAccountHistory.setBizType(bizType);
        assistantAccountHistory.setBizItem(bizItem);
        assistantAccountHistory.setOrderId(MySeq.getChangeAccount());
        assistantAccountHistory.setFundDirection(MchConstant.FUND_DIRECTION_SUB);

        // 账户余额
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andBalanceEqualTo(assistantAccount.getBalance());
        criteria.andSettAmountEqualTo(assistantAccount.getSettAmount());

        // 数据操作,保证事务
        updateAccountAmount4Transactional(assistantAccountHistory, updateAssistantAccount, example);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public void debit2Account(Long assistantId, Byte bizType, Long amount, String bizItem, String card) {
        AssistantAccount assistantAccount = findAvailableAccount(assistantId);

        Long availableBalance = assistantAccount.getAvailableBalance();
        if(availableBalance.compareTo(amount) == -1) {
            // 余额不足减款
            throw ServiceException.build(RetEnum.RET_SERVICE_ACCOUNT_BALANCE_OUT_LIMIT);
        }

        AssistantAccount updateAssistantAccount = new AssistantAccount();

        // 更新后账户余额
        updateAssistantAccount.setBalance(assistantAccount.getBalance() - amount);
        // 更新后可结算金额
        updateAssistantAccount.setSettAmount(assistantAccount.getSettAmount() - amount);

        // 记录资金流水
        AssistantAccountHistory assistantAccountHistory = new AssistantAccountHistory();
        assistantAccountHistory.setAssistantId(assistantId);
        assistantAccountHistory.setAmount(amount);
        assistantAccountHistory.setBalance(assistantAccount.getBalance());
        assistantAccountHistory.setAfterBalance(assistantAccount.getBalance() - amount);
        assistantAccountHistory.setBizType(bizType);
        assistantAccountHistory.setBizItem(bizItem);
//        assistantAccountHistory.setOrderId(MySeq.getChangeAccount());
        assistantAccountHistory.setOrderId("卡：" + card);
        assistantAccountHistory.setFundDirection(MchConstant.FUND_DIRECTION_SUB);

        // 账户余额
        AssistantAccountExample example = new AssistantAccountExample();
        AssistantAccountExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andBalanceEqualTo(assistantAccount.getBalance());
        criteria.andSettAmountEqualTo(assistantAccount.getSettAmount());

        // 数据操作,保证事务
        updateAccountAmount4Transactional(assistantAccountHistory, updateAssistantAccount, example);
    }

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    private void updateAccountAmount4Transactional(AssistantAccountHistory assistantAccountHistory, AssistantAccount assistantAccount, AssistantAccountExample example) {
        assistantAccountHistoryMapper.insertSelective(assistantAccountHistory);
        int updateCount = assistantAccountMapper.updateByExampleSelective(assistantAccount, example);
        if(updateCount != 1) {
            // 更新失败,抛出异常
            throw ServiceException.build(RetEnum.RET_SERVICE_ACCOUNT_AMOUNT_UPDATE_FAIL);
        }
    }

    /**
     * 查找可用账户(代理商账户存在且状态正常)
     * @param assistantId
     * @return
     */
    private AssistantAccount findAvailableAccount(Long assistantId) {
        AssistantAccount assistantAccount = findByAssistantId(assistantId);
        if(assistantAccount == null || assistantAccount.getStatus() != MchConstant.PUB_YES) {
            // 账户不存在
            throw ServiceException.build(RetEnum.RET_AGENT_ACCOUNT_NOT_EXIST);
        }
        return assistantAccount;
    }

    @Override
    public BigDecimal sumAssistantBalance(AssistantAccount record) {
        AssistantAccountExample exa = new AssistantAccountExample();
        AssistantAccountExample.Criteria c = exa.createCriteria();
        if(record!= null && record.getAssistantId() != null){
            c.andAssistantIdEqualTo(record.getAssistantId());
        }
        return assistantAccountMapper.sumAssistantBalance(exa);
    }


}
