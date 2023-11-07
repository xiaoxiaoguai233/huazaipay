package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZSlrjAccount;
import org.xxpay.core.service.IZSlrjAccountService;
import org.xxpay.service.dao.mapper.ZSlrjAccountMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.IZSlrjAccountService", version = "1.0.0", retries = -1)
public class ZSlrjAccountServiceImpl implements IZSlrjAccountService {

    private static final MyLog _log = MyLog.getLog(ZSlrjAccountServiceImpl.class);

    @Autowired
    ZSlrjAccountMapper zSlrjAccountMapper;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ZSlrjAccount selectReceiveAccountByUpdateTime() {
        ZSlrjAccount zSlrjAccount = zSlrjAccountMapper.selectReceiveAccountByUpdateTime();
        // 无数据的情况下会为null，指的是当前没有收款账号了
        if(zSlrjAccount == null){
            return null;
        }
        zSlrjAccountMapper.updateReceiveAccountByUpdateTime(zSlrjAccount);
        return zSlrjAccount;
    }

    @Override
    public List<ZSlrjAccount> selectList(ZSlrjAccount zSlrjAccount, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZSlrjAccount> zSlrjAccounts = zSlrjAccountMapper.selectList(zSlrjAccount, page, limit);
        PageInfo<ZSlrjAccount> pageInfo = new PageInfo<ZSlrjAccount>(zSlrjAccounts);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zSlrjAccountMapper.selectListCount(parentId);
    }

    @Override
    public ZSlrjAccount selectByPhone(String phone) {
        return zSlrjAccountMapper.selectByPhone(phone);
    }

    @Override
    public ZSlrjAccount selectById(Long id) {
        return zSlrjAccountMapper.selectById(id);
    }

    @Override
    public int update(ZSlrjAccount zSlrjAccount) {
        return zSlrjAccountMapper.update(zSlrjAccount);
    }

    @Override
    public int insert(ZSlrjAccount zSlrjAccount) {
        return zSlrjAccountMapper.insert(zSlrjAccount);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zSlrjAccountMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int clearTodayMoney() {
        return zSlrjAccountMapper.clearTodayMoney();
    }
}

