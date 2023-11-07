package org.xxpay.core.service;

import org.xxpay.core.entity.AssistantInfo;

import java.util.List;
import java.util.Map;

public interface IAssistantInfoService {

    int add(AssistantInfo AssistantInfo);

    int update(AssistantInfo AssistantInfo);

    AssistantInfo find(AssistantInfo AssistantInfo);

    AssistantInfo findByLoginName(String loginName);

    AssistantInfo findByAssistantId(Long AssistantId);

    AssistantInfo findByUserName(String userName);

    AssistantInfo findByMobile(Long mobile);

    AssistantInfo findByEmail(String email);

    List<AssistantInfo> select(int offset, int limit, AssistantInfo AssistantInfo);

    Integer count(AssistantInfo AssistantInfo);

    Map count4Assistant();

    AssistantInfo findByParentAssistantId(Long parentAssistantId);

    /**
     * 重新构建代理商信息
     * @param info
     */
    AssistantInfo reBuildAssistantInfoSettConfig(AssistantInfo info);

    /**
     * 得到代理商风险预存期
     * @param AssistantId
     * @return
     */
    int getRiskDay(Long AssistantId);

    List<AssistantInfo> selectAll(AssistantInfo AssistantInfo);
}
