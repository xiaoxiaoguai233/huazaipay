package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.AssistantAccountHistory;
import org.xxpay.core.entity.AssistantAccountHistoryExample;

import java.util.List;
import java.util.Map;

public interface AssistantAccountHistoryMapper {

    int countByExample(AssistantAccountHistoryExample example);

    int deleteByExample(AssistantAccountHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AssistantAccountHistory record);

    int insertSelective(AssistantAccountHistory record);

    List<AssistantAccountHistory> selectByExample(AssistantAccountHistoryExample example);

    AssistantAccountHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AssistantAccountHistory record, @Param("example") AssistantAccountHistoryExample example);

    int updateByExample(@Param("record") AssistantAccountHistory record, @Param("example") AssistantAccountHistoryExample example);

    int updateByPrimaryKeySelective(AssistantAccountHistory record);

    int updateByPrimaryKey(AssistantAccountHistory record);

    /**
     * 统计代理商分润数据
     * @param param
     * @return
     */
    List<Map> count4AssistantProfit(Map param);
}
