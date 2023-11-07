package org.xxpay.core.service;

import org.xxpay.core.entity.AssistantAccountHistory;

import java.util.List;
import java.util.Map;

public interface IAssistantAccountHistoryService {
    List<AssistantAccountHistory> select(int offset, int limit, org.xxpay.core.entity.AssistantAccountHistory assistantAccountHistory);

    int count(AssistantAccountHistory assistantAccountHistory);

    AssistantAccountHistory findById(Long id);

    AssistantAccountHistory findByAssistantIdAndId(Long assistantId, Long id);

    /**
     * 统计代理商分润
     * @return
     */
    List<Map> count4AssistantProfit(Long assistantId);
}
