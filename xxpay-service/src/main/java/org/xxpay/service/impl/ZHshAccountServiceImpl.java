package org.xxpay.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZHshAccount;
import org.xxpay.core.service.IZHshAccountService;
import org.xxpay.service.dao.mapper.ZHshAccountMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.IZHshAccountService", version = "1.0.0", retries = -1)
public class ZHshAccountServiceImpl implements IZHshAccountService {

    private static final MyLog _log = MyLog.getLog(ZHshAccountServiceImpl.class);

    @Autowired ZHshAccountMapper zHshAccountMapper;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ZHshAccount selectReceiveAccountByUpdateTime() {

        ZHshAccount zHshAccount = zHshAccountMapper.selectReceiveAccountByUpdateTime();

        // 无数据的情况下会为null，指的是当前没有收款账号了
        if(zHshAccount == null){
            return null;
        }
        int result = zHshAccountMapper.updateReceiveAccountByUpdateTime(zHshAccount);
        // 数据更新时间失败
        if(result == 0){
            return null;
        }
        return zHshAccount;
    }

    @Override
    public List<ZHshAccount> selectList(ZHshAccount zHshAccount, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZHshAccount> zHshAccounts = zHshAccountMapper.selectList(zHshAccount, page, limit);
        PageInfo<ZHshAccount> pageInfo = new PageInfo<ZHshAccount>(zHshAccounts);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zHshAccountMapper.selectListCount(parentId);
    }

    @Override
    public ZHshAccount selectByPhone(String phone) {
        return zHshAccountMapper.selectByPhone(phone);
    }

    @Override
    public ZHshAccount selectById(Long id) {
        return zHshAccountMapper.selectById(id);
    }

    @Override
    public int update(ZHshAccount zHshAccount) {
        return zHshAccountMapper.update(zHshAccount);
    }

    @Override
    public int insert(ZHshAccount zHshAccount) {
        return zHshAccountMapper.insert(zHshAccount);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zHshAccountMapper.deleteByPrimaryKey(id);
    }
}
