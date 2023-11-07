package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZFkShop;
import org.xxpay.core.entity.ZSlrjAccount;
import org.xxpay.core.service.IZFkKamiShopService;
import org.xxpay.service.dao.mapper.ZFkKamiShopMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.IZFkKamiShopService", version = "1.0.0", retries = -1)
public class ZFkKamiShopServiceImpl implements IZFkKamiShopService {

    private static final MyLog _log = MyLog.getLog(ZFkKamiShopServiceImpl.class);

    @Autowired
    ZFkKamiShopMapper zFkKamiShopMapper;

    @Override
    public List<ZFkShop> selectListByPay() {
        return zFkKamiShopMapper.selectListByPay();
    }

    @Override
    public List<ZFkShop> selectList(ZFkShop zFkShop, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZFkShop> zFkShopList = zFkKamiShopMapper.selectList(zFkShop, page, limit);
        PageInfo<ZFkShop> pageInfo = new PageInfo<ZFkShop>(zFkShopList);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zFkKamiShopMapper.selectListCount(parentId);
    }

    @Override
    public ZFkShop selectByName(String name) {
        return zFkKamiShopMapper.selectByName(name);
    }

    @Override
    public ZFkShop selectById(Long id) {
        return zFkKamiShopMapper.selectById(id);
    }

    @Override
    public int update(ZFkShop zFkShop) {
        return zFkKamiShopMapper.update(zFkShop);
    }

    @Override
    public int insert(ZFkShop zFkShop) {
        return zFkKamiShopMapper.insert(zFkShop);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zFkKamiShopMapper.deleteByPrimaryKey(id);
    }

}
