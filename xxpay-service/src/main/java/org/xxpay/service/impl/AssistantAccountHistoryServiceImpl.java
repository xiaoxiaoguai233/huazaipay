package org.xxpay.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.xxpay.core.entity.AssistantAccountHistory;
import org.xxpay.core.entity.AssistantAccountHistoryExample;
import org.xxpay.core.service.IAssistantAccountHistoryService;
import org.xxpay.service.dao.mapper.AssistantAccountHistoryMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(version = "1.0.0")
public class AssistantAccountHistoryServiceImpl implements IAssistantAccountHistoryService {

    @Autowired
    private AssistantAccountHistoryMapper assistantAccountHistoryMapper;

    @Override
    public List<AssistantAccountHistory> select(int offset, int limit, AssistantAccountHistory assistantAccountHistory) {
        AssistantAccountHistoryExample example = new AssistantAccountHistoryExample();
        example.setOrderByClause("createTime DESC");
        example.setOffset(offset);
        example.setLimit(limit);
        AssistantAccountHistoryExample.Criteria criteria = example.createCriteria();
        setCriteria(criteria, assistantAccountHistory);
        return assistantAccountHistoryMapper.selectByExample(example);
    }

    @Override
    public int count(AssistantAccountHistory assistantAccountHistory) {
        AssistantAccountHistoryExample example = new AssistantAccountHistoryExample();
        AssistantAccountHistoryExample.Criteria criteria = example.createCriteria();
        setCriteria(criteria, assistantAccountHistory);
        return assistantAccountHistoryMapper.countByExample(example);
    }

    @Override
    public AssistantAccountHistory findById(Long id) {
        return assistantAccountHistoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public AssistantAccountHistory findByAssistantIdAndId(Long assistantId, Long id) {
        AssistantAccountHistoryExample example = new AssistantAccountHistoryExample();
        AssistantAccountHistoryExample.Criteria criteria = example.createCriteria();
        criteria.andAssistantIdEqualTo(assistantId);
        criteria.andIdEqualTo(id);
        List<AssistantAccountHistory> assistantAccountHistoryList = assistantAccountHistoryMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(assistantAccountHistoryList)) return null;
        return assistantAccountHistoryList.get(0);
    }

    @Override
    public List<Map> count4AssistantProfit(Long assistantId) {
        Map param = new HashMap<>();
        if(assistantId != null) param.put("assistantId", assistantId);
        return assistantAccountHistoryMapper.count4AssistantProfit(param);
    }

    void setCriteria(AssistantAccountHistoryExample.Criteria criteria, AssistantAccountHistory assistantAccountHistory) {
        if(assistantAccountHistory != null) {
            if(assistantAccountHistory.getId() != null) criteria.andIdEqualTo(assistantAccountHistory.getId());
            if(assistantAccountHistory.getAssistantId() != null) criteria.andAssistantIdEqualTo(assistantAccountHistory.getAssistantId());
            if(assistantAccountHistory.getFundDirection() != null && !"-99".equals(assistantAccountHistory.getFundDirection())) criteria.andFundDirectionEqualTo(assistantAccountHistory.getFundDirection());
            if(assistantAccountHistory.getBizType() != null && -99 != assistantAccountHistory.getBizType()) criteria.andBizTypeEqualTo(assistantAccountHistory.getBizType());
        }
    }

}
