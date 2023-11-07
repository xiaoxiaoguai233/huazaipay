package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZMthAccount;
import org.xxpay.core.service.IZMthAccountService;
import org.xxpay.service.dao.mapper.ZMthAccountMapper;

import java.util.List;


@Service(interfaceName = "org.xxpay.core.service.IZMthAccountService", version = "1.0.0", retries = -1)
public class ZMthAccountServiceImpl implements IZMthAccountService {

    private static final MyLog _log = MyLog.getLog(ZMthAccountServiceImpl.class);

    @Autowired
    ZMthAccountMapper zMthAccountMapper;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ZMthAccount selectReceiveAccountByUpdateTime() {
        ZMthAccount zHshAccount = zMthAccountMapper.selectReceiveAccountByUpdateTime();
        // 无数据的情况下会为null，指的是当前没有收款账号了
        if(zHshAccount == null){
            return null;
        }
        zMthAccountMapper.updateReceiveAccountByUpdateTime(zHshAccount);
        return zHshAccount;
    }

    @Override
    public List<ZMthAccount> selectList(ZMthAccount zMthAccount, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZMthAccount> zMthAccounts = zMthAccountMapper.selectList(zMthAccount, page, limit);
        PageInfo<ZMthAccount> pageInfo = new PageInfo<ZMthAccount>(zMthAccounts);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zMthAccountMapper.selectListCount(parentId);
    }

    @Override
    public ZMthAccount selectByPhone(String phone) {
        return zMthAccountMapper.selectByPhone(phone);
    }

    @Override
    public ZMthAccount selectById(Long id) {
        return zMthAccountMapper.selectById(id);
    }

    @Override
    public int update(ZMthAccount zHshAccount) {
        return zMthAccountMapper.update(zHshAccount);
    }

    @Override
    public int insert(ZMthAccount zHshAccount) {
        return zMthAccountMapper.insert(zHshAccount);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zMthAccountMapper.deleteByPrimaryKey(id);
    }
}

