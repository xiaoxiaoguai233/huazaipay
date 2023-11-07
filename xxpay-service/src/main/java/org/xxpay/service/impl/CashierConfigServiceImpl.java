package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.xxpay.core.entity.CashierConfig;
import org.xxpay.core.entity.CashierConfigExample;
import org.xxpay.core.service.ICashierConfigService;
import org.xxpay.service.dao.mapper.CashierConfigMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.ICashierConfigService", version = "1.0.0", retries = -1)
public class CashierConfigServiceImpl implements ICashierConfigService {

    @Autowired
    private CashierConfigMapper cashierConfigMapper;


    @Override
    public CashierConfig get(Long merchantId) {
        CashierConfigExample cashierConfigExample = new CashierConfigExample();
        CashierConfigExample.Criteria criteria = cashierConfigExample.createCriteria();
        criteria.andMerchantidEqualTo(merchantId);
        List<CashierConfig> cashierConfigs = cashierConfigMapper.selectByExample(cashierConfigExample);
        return cashierConfigs.isEmpty() ? null : cashierConfigs.get(0);
    }

    @Override
    public Integer update(CashierConfig cashierConfig) {
        CashierConfigExample example = new CashierConfigExample();
        CashierConfigExample.Criteria criteria = example.createCriteria();
        criteria.andMerchantidEqualTo(cashierConfig.getMerchantid());
        return cashierConfigMapper.updateByExampleSelective(cashierConfig, example);
    }

    @Override
    public Integer add(CashierConfig cashierConfig) {
        return cashierConfigMapper.insertSelective(cashierConfig);
    }
}
