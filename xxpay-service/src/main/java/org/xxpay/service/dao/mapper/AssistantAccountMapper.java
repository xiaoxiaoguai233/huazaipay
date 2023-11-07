package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.AssistantAccount;
import org.xxpay.core.entity.AssistantAccountExample;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AssistantAccountMapper  {
    int countByExample(AssistantAccountExample example);

    int deleteByExample(AssistantAccountExample example);

    int deleteByPrimaryKey(Long AssistantId);

    int insert(AssistantAccount record);

    int insertSelective(AssistantAccount record);

    List<AssistantAccount> selectByExample(AssistantAccountExample example);

    AssistantAccount selectByPrimaryKey(@Param("AssistantId") Long AssistantId);

    int updateByExampleSelective(@Param("record") AssistantAccount record, @Param("example") AssistantAccountExample example);

    int updateByExample(@Param("record") AssistantAccount record, @Param("example") AssistantAccountExample example);

    int updateByPrimaryKeySelective(AssistantAccount record);

    int updateByPrimaryKey(AssistantAccount record);

    /**
     * 更新代理商账户结算金额
     * @param map
     * @return
     */
    int updateSettAmount(Map map);

    BigDecimal sumAssistantBalance(AssistantAccountExample exa);

}