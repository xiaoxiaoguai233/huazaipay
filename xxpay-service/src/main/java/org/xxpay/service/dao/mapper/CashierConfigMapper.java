package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.CashierConfig;
import org.xxpay.core.entity.CashierConfigExample;

import java.util.List;

public interface CashierConfigMapper {
    int countByExample(CashierConfigExample example);

    int deleteByExample(CashierConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(CashierConfig record);

    int insertSelective(CashierConfig record);

    List<CashierConfig> selectByExample(CashierConfigExample example);

    CashierConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") CashierConfig record, @Param("example") CashierConfigExample example);

    int updateByExample(@Param("record") CashierConfig record, @Param("example") CashierConfigExample example);

    int updateByPrimaryKeySelective(CashierConfig record);

    int updateByPrimaryKey(CashierConfig record);
}