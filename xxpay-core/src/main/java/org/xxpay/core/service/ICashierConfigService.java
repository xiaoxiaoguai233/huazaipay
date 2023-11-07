package org.xxpay.core.service;

import org.xxpay.core.entity.CashierConfig;

/**
 * @author: dingzhiwei
 * @date: 18/9/11
 * @description: 获取收银台配置信息
 */
public interface ICashierConfigService {

    public CashierConfig get(Long merchantId);

    public Integer update(CashierConfig cashierConfig);

    public Integer add(CashierConfig cashierConfig);

}
