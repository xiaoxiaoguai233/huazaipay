package org.xxpay.core.service;

import org.xxpay.core.entity.MchConfig;

import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 2018/5/29
 * @description:
 */
public interface IXxPayMchConfigService {
    /**
     * @param mchConfig
     */
    public void add(MchConfig mchConfig);

    /**
     * @param mchConfig
     */
    public void update(MchConfig mchConfig);


    /**
     * @param mchId
     * @return
     */
    MchConfig get(Long mchId,String key);

    /**
     * @return
     */
    List<MchConfig> list(Long mchId,Byte configType);

    /**
     *
     * @return
     */
    MchConfig getGlobal(String key);

    /**
     * 编辑根据ID获取信息
     */
    MchConfig getById(Long Id);

    /**
     * 编辑根据ID获取信息
     */
    List<MchConfig> getByMchId(Long mchId);

}
