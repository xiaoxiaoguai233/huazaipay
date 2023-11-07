package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.AssistantInfo;
import org.xxpay.core.entity.AssistantInfoExample;

import java.util.List;
import java.util.Map;

public interface AssistantInfoMapper  {
    int countByExample(AssistantInfoExample example);

    int deleteByExample(AssistantInfoExample example);

    int deleteByPrimaryKey(Long AssistantId);

    int insert(AssistantInfo record);

    int insertSelective(AssistantInfo record);

    List<AssistantInfo> selectByExample(AssistantInfoExample example);

    AssistantInfo selectByPrimaryKey(Long AssistantId);

    int updateByExampleSelective(@Param("record") AssistantInfo record, @Param("example") AssistantInfoExample example);

    int updateByExample(@Param("record") AssistantInfo record, @Param("example") AssistantInfoExample example);

    int updateByPrimaryKeySelective(AssistantInfo record);

    int updateByPrimaryKey(AssistantInfo record);

    /**
     * 统计代理商信息
     * @param param
     * @return
     */
    Map count4Assistant(Map param);
}
