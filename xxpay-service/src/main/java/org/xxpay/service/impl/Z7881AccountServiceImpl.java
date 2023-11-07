package org.xxpay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.xxpay.core.entity.Z7881Account;
import org.xxpay.core.service.IZ7881AccountService;
import org.xxpay.service.dao.mapper.Z7881AccountMapper;


/**
 * <p>
 * 7881账号 服务实现类
 * </p>
 *
 * @author [mybatis plus generator]
 * @since 2023-01-22
 */
@Service
public class Z7881AccountServiceImpl extends ServiceImpl<Z7881AccountMapper, Z7881Account> implements IZ7881AccountService {

    @Override
    public IPage<Z7881Account> listByPage(IPage iPage, Z7881Account z7881Account, JSONObject paramJSON, LambdaQueryWrapper<Z7881Account> wrapper) {

        System.out.println("+++++++++++++++++++++++++++4");
        if (z7881Account.getId() != null && z7881Account.getId() <= 0) {
            wrapper.eq(Z7881Account::getId, z7881Account.getId());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(z7881Account.getToken())) {
            wrapper.eq(Z7881Account::getToken, z7881Account.getToken());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(z7881Account.getPhone())) {
            wrapper.eq(Z7881Account::getPhone, z7881Account.getPhone());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(z7881Account.getPhone())) {
            wrapper.eq(Z7881Account::getPhone, z7881Account.getPhone());
        }
        if (z7881Account.getParentId() != null && z7881Account.getParentId() != 0) {
            wrapper.eq(Z7881Account::getParentId, z7881Account.getParentId());
        }
        if(z7881Account.getState() != null && z7881Account.getState() >= 0 && z7881Account.getState() <= 2){
            wrapper.eq(Z7881Account::getState, z7881Account.getState());
        }

        wrapper.orderByDesc(Z7881Account::getCreateTime);
        return page(iPage, wrapper);
    }
}
