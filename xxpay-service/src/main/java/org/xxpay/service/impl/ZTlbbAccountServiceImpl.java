package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZTlbbAccount;
import org.xxpay.core.service.IZTlbbAccountService;
import org.xxpay.service.dao.mapper.ZTlbbAccountMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.IZTlbbAccountService", version = "1.0.0", retries = -1)
public class ZTlbbAccountServiceImpl implements IZTlbbAccountService {

    private static final MyLog _log = MyLog.getLog(ZTlbbAccountServiceImpl.class);

    @Autowired
    ZTlbbAccountMapper zTlbbAccountMapper;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ZTlbbAccount selectReceiveAccountByUpdateTime() {
        ZTlbbAccount zTlbbAccount = zTlbbAccountMapper.selectReceiveAccountByUpdateTime();
        // 无数据的情况下会为null，指的是当前没有收款账号了
        if(zTlbbAccount == null){
            return null;
        }
        zTlbbAccountMapper.updateReceiveAccountByUpdateTime(zTlbbAccount);
        return zTlbbAccount;
    }

    @Override
    public List<ZTlbbAccount> selectList(ZTlbbAccount zTlbbAccount, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZTlbbAccount> zTlbbAccounts = zTlbbAccountMapper.selectList(zTlbbAccount, page, limit);
        PageInfo<ZTlbbAccount> pageInfo = new PageInfo<ZTlbbAccount>(zTlbbAccounts);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zTlbbAccountMapper.selectListCount(parentId);
    }

    @Override
    public ZTlbbAccount selectByPhone(String phone) {
        return zTlbbAccountMapper.selectByPhone(phone);
    }

    @Override
    public ZTlbbAccount selectByToken(String token) {
        return zTlbbAccountMapper.selectByToken(token);
    }

    @Override
    public ZTlbbAccount selectById(Long id) {
        return zTlbbAccountMapper.selectById(id);
    }

    @Override
    public int update(ZTlbbAccount zTlbbAccount) {
        return zTlbbAccountMapper.update(zTlbbAccount);
    }

    @Override
    public int insert(ZTlbbAccount zTlbbAccount) {
        return zTlbbAccountMapper.insert(zTlbbAccount);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zTlbbAccountMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int clearTodayMoney() {
        return zTlbbAccountMapper.clearTodayMoney();
    }
}


