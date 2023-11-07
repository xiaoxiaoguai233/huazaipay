package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZHxtAccount;
import org.xxpay.core.service.IZHxtAccountService;
import org.xxpay.service.dao.mapper.ZHxtAccountMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.IZHxtAccountService", version = "1.0.0", retries = -1)
public class ZHxtAccountServiceImpl implements IZHxtAccountService {
    private static final MyLog _log = MyLog.getLog(ZHxtAccountServiceImpl.class);

    @Autowired
    ZHxtAccountMapper zHxtAccountMapper;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ZHxtAccount selectReceiveAccountByUpdateTime() {

        ZHxtAccount zHxtAccount = zHxtAccountMapper.selectReceiveAccountByUpdateTime();

        // 无数据的情况下会为null，指的是当前没有收款账号了
        if(zHxtAccount == null){
            return null;
        }
        int result = zHxtAccountMapper.updateReceiveAccountByUpdateTime(zHxtAccount);
        // 数据更新时间失败
        if(result == 0){
            return null;
        }
        return zHxtAccount;
    }

    @Override
    public List<ZHxtAccount> selectList(ZHxtAccount zHshAccount, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZHxtAccount> zHxtAccounts = zHxtAccountMapper.selectList(zHshAccount, page, limit);
        PageInfo<ZHxtAccount> pageInfo = new PageInfo<ZHxtAccount>(zHxtAccounts);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zHxtAccountMapper.selectListCount(parentId);
    }

    @Override
    public ZHxtAccount selectByPhone(String phone) {
        return zHxtAccountMapper.selectByPhone(phone);
    }

    @Override
    public ZHxtAccount selectById(Long id) {
        return zHxtAccountMapper.selectById(id);
    }

    @Override
    public int update(ZHxtAccount zHshAccount) {
        return zHxtAccountMapper.update(zHshAccount);
    }

    @Override
    public int insert(ZHxtAccount zHshAccount) {
        return zHxtAccountMapper.insert(zHshAccount);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zHxtAccountMapper.deleteByPrimaryKey(id);
    }
}
