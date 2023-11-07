package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.StrUtil;
import org.xxpay.core.entity.AssistantAccount;
import org.xxpay.core.entity.AssistantInfo;
import org.xxpay.core.entity.AssistantInfoExample;
import org.xxpay.core.service.IAssistantInfoService;
import org.xxpay.core.service.ISysConfigService;
import org.xxpay.service.dao.mapper.AssistantAccountMapper;
import org.xxpay.service.dao.mapper.AssistantInfoMapper;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "org.xxpay.core.service.IAssistantInfoService", version = "1.0.0", retries = -1)
public class AssistantInfoServiceImpl implements IAssistantInfoService {

    @Autowired
    private AssistantInfoMapper AssistantInfoMapper;

    @Autowired
    private AssistantAccountMapper AssistantAccountMapper;

    @Autowired
    private ISysConfigService sysConfigService;

    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    @Override
    public int add(AssistantInfo AssistantInfo) {
        // 插入码商基本信息
        AssistantInfoMapper.insertSelective(AssistantInfo);
        // 插入码商账户信息
        AssistantAccount AssistantAccount = new AssistantAccount();
        AssistantAccount.setAssistantId(AssistantInfo.getAssistantId());
        AssistantAccount.setBalance(BigDecimal.ZERO.longValue());
        AssistantAccount.setSecurityMoney(BigDecimal.ZERO.longValue());
        AssistantAccount.setSettAmount(BigDecimal.ZERO.longValue());
        AssistantAccount.setUnBalance(BigDecimal.ZERO.longValue());
        AssistantAccount.setTodayExpend(BigDecimal.ZERO.longValue());
        AssistantAccount.setTodayIncome(BigDecimal.ZERO.longValue());
        AssistantAccount.setTotalExpend(BigDecimal.ZERO.longValue());
        AssistantAccount.setTotalIncome(BigDecimal.ZERO.longValue());

        System.out.println(AssistantAccount.toString());
        System.out.println(AssistantAccount.toString());
        System.out.println(AssistantAccount.toString());
        System.out.println(AssistantAccount.toString());
        System.out.println(AssistantAccount.toString());
        return AssistantAccountMapper.insertSelective(AssistantAccount);
    }

    @Override
    public int update(AssistantInfo AssistantInfo) {
        return AssistantInfoMapper.updateByPrimaryKeySelective(AssistantInfo);
    }

    @Override
    public AssistantInfo find(AssistantInfo AssistantInfo) {
        AssistantInfoExample example = new AssistantInfoExample();
        AssistantInfoExample.Criteria criteria = example.createCriteria();
        setCriteria(criteria, AssistantInfo);
        List<AssistantInfo> AssistantInfoList = AssistantInfoMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(AssistantInfoList)) return null;
        return AssistantInfoList.get(0);
    }

    @Override
    public AssistantInfo findByLoginName(String loginName) {
        if(StringUtils.isBlank(loginName)) return null;
        AssistantInfo AssistantInfo;
        if(StrUtil.checkEmail(loginName)) {
            AssistantInfo = findByEmail(loginName);
            if(AssistantInfo != null) return AssistantInfo;
        }
        if(StrUtil.checkMobileNumber(loginName)) {
            AssistantInfo = findByMobile(Long.parseLong(loginName));
            if(AssistantInfo != null) return AssistantInfo;
        }
        if(NumberUtils.isDigits(loginName)) {
            AssistantInfo = findByAssistantId(Long.parseLong(loginName));
            if(AssistantInfo != null) return AssistantInfo;
        }
        AssistantInfo = findByUserName(loginName);
        return AssistantInfo;
    }

    @Override
    public AssistantInfo findByAssistantId(Long AssistantId) {
        return AssistantInfoMapper.selectByPrimaryKey(AssistantId);
    }

    @Override
    public AssistantInfo findByUserName(String userName) {
        AssistantInfo AssistantInfo = new AssistantInfo();
        AssistantInfo.setUserName(userName);
        return find(AssistantInfo);
    }

    @Override
    public AssistantInfo findByMobile(Long mobile) {
        AssistantInfo AssistantInfo = new AssistantInfo();
        AssistantInfo.setMobile(mobile);
        return find(AssistantInfo);
    }

    @Override
    public AssistantInfo findByEmail(String email) {
        AssistantInfo AssistantInfo = new AssistantInfo();
        AssistantInfo.setEmail(email);
        return find(AssistantInfo);
    }

    @Override
    public AssistantInfo findByParentAssistantId(Long parentAssistantId) {
        AssistantInfo AssistantInfo = new AssistantInfo();
        AssistantInfo.setParentAssistantId(parentAssistantId);
        return find(AssistantInfo);
    }

    @Override
    public List<AssistantInfo> select(int offset, int limit, AssistantInfo AssistantInfo) {
        AssistantInfoExample example = new AssistantInfoExample();
        example.setOrderByClause("createTime DESC");
        example.setOffset(offset);
        example.setLimit(limit);
        AssistantInfoExample.Criteria criteria = example.createCriteria();
        setCriteria(criteria, AssistantInfo);
        return AssistantInfoMapper.selectByExample(example);
    }

    @Override
    public Integer count(AssistantInfo AssistantInfo) {
        AssistantInfoExample example = new AssistantInfoExample();
        AssistantInfoExample.Criteria criteria = example.createCriteria();
        setCriteria(criteria, AssistantInfo);
        return AssistantInfoMapper.countByExample(example);
    }

    @Override
    public Map count4Assistant() {
        Map param = new HashMap<>();
        return AssistantInfoMapper.count4Assistant(param);
    }

    @Override
    public AssistantInfo reBuildAssistantInfoSettConfig(AssistantInfo info) {
        // 继承系统,按系统配置设置结算属性
        if(info.getSettConfigMode() == 1) {
            JSONObject settObj = sysConfigService.getSysConfigObj("sett");
            info.setDrawFlag(settObj.getByte("drawFlag"));
            info.setAllowDrawWeekDay(settObj.getString("allowDrawWeekDay"));
            info.setDrawDayStartTime(settObj.getString("drawDayStartTime"));
            info.setDrawDayEndTime(settObj.getString("drawDayEndTime"));
            info.setDayDrawTimes(settObj.getInteger("dayDrawTimes"));
            info.setDrawMaxDayAmount(settObj.getLong("drawMaxDayAmount"));
            info.setMaxDrawAmount(settObj.getLong("maxDrawAmount"));
            info.setMinDrawAmount(settObj.getLong("minDrawAmount"));
            info.setFeeType(settObj.getByte("feeType"));
            info.setFeeRate(settObj.getBigDecimal("feeRate"));
            info.setFeeLevel(settObj.getString("feeLevel"));
            info.setDrawFeeLimit(settObj.getLong("drawFeeLimit"));
            info.setSettMode(settObj.getByte("settMode"));
        }
        return info;
    }

    @Override
    public int getRiskDay(Long AssistantId) {
        AssistantInfo AssistantInfo = findByAssistantId(AssistantId);
        if(AssistantInfo == null) return 1;
        AssistantInfo = reBuildAssistantInfoSettConfig(AssistantInfo);
        if(AssistantInfo.getSettMode() == 1) {
            return 0;
        }
        return 1;
    }

    @Override
    public List<AssistantInfo> selectAll(AssistantInfo AssistantInfo) {
        AssistantInfoExample example = new AssistantInfoExample();
        example.setOrderByClause("AssistantLevel ASC");
        AssistantInfoExample.Criteria criteria = example.createCriteria();
        setCriteria(criteria,AssistantInfo);
        return AssistantInfoMapper.selectByExample(example);
    }

    void setCriteria(AssistantInfoExample.Criteria criteria, AssistantInfo AssistantInfo) {
        if(AssistantInfo != null) {
            if(AssistantInfo.getAssistantId() != null) criteria.andAssistantIdEqualTo(AssistantInfo.getAssistantId());
            if(AssistantInfo.getParentAssistantId() != null) criteria.andParentAssistantIdEqualTo(AssistantInfo.getParentAssistantId());
            if(AssistantInfo.getEmail() != null) criteria.andEmailEqualTo(AssistantInfo.getEmail());
            if(AssistantInfo.getMobile() != null) criteria.andMobileEqualTo(AssistantInfo.getMobile());
            if(AssistantInfo.getUserName() != null) criteria.andUserNameEqualTo(AssistantInfo.getUserName());
            if(AssistantInfo.getStatus() != null && AssistantInfo.getStatus().byteValue() != -99) criteria.andStatusEqualTo(AssistantInfo.getStatus());
            if(AssistantInfo.getAssistantLevel() != null) criteria.andAssistantLevelEqualTo(AssistantInfo.getAssistantLevel());
        }
    }
}

