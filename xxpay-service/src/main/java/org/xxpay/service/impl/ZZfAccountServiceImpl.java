package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZZfAccount;
import org.xxpay.core.service.IZZfAccountService;
import org.xxpay.service.dao.mapper.ZZfAccountMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.IZZfAccountService", version = "1.0.0", retries = -1)
public class ZZfAccountServiceImpl implements IZZfAccountService {

    private static final MyLog _log = MyLog.getLog(ZZfAccountServiceImpl.class);

    @Autowired
    ZZfAccountMapper zZfAccountMapper;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ZZfAccount selectReceiveAccountByUpdateTime() {
        ZZfAccount zZfAccount = zZfAccountMapper.selectReceiveAccountByUpdateTime();
        // 无数据的情况下会为null，指的是当前没有收款账号了
        if(zZfAccount == null){
            return null;
        }
        zZfAccountMapper.updateReceiveAccountByUpdateTime(zZfAccount);
        return zZfAccount;
    }

    @Override
    public List<ZZfAccount> selectList(ZZfAccount zZfAccount, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZZfAccount> zZfAccounts = zZfAccountMapper.selectList(zZfAccount, page, limit);
        PageInfo<ZZfAccount> pageInfo = new PageInfo<ZZfAccount>(zZfAccounts);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zZfAccountMapper.selectListCount(parentId);
    }

    @Override
    public ZZfAccount selectByPhone(String phone) {
        return zZfAccountMapper.selectByPhone(phone);
    }

    @Override
    public ZZfAccount selectByToken(String token) {
        return zZfAccountMapper.selectByToken(token);
    }

    @Override
    public ZZfAccount selectById(Long id) {
        return zZfAccountMapper.selectById(id);
    }

    @Override
    public int update(ZZfAccount zZfAccount) {
        return zZfAccountMapper.update(zZfAccount);
    }

    @Override
    public int insert(ZZfAccount zZfAccount) {
        return zZfAccountMapper.insert(zZfAccount);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zZfAccountMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int clearTodayMoney() {
        return zZfAccountMapper.clearTodayMoney();
    }
}


