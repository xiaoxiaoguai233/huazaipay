package org.xxpay.service.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.xxpay.core.entity.MchConfig;
import org.xxpay.core.entity.MchConfigExample;

import java.util.List;

public interface MchConfigMapper {
    int countByExample(MchConfigExample example);

    int deleteByExample(MchConfigExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MchConfig record);

    int insertSelective(MchConfig record);

    List<MchConfig> selectByExample(MchConfigExample example);

    MchConfig selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MchConfig record, @Param("example") MchConfigExample example);

    int updateByExample(@Param("record") MchConfig record, @Param("example") MchConfigExample example);

    int updateByPrimaryKeySelective(MchConfig record);

    int updateByPrimaryKey(MchConfig record);
}