package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.ZFkKami;
import org.xxpay.core.service.IZFkKamiService;
import org.xxpay.service.dao.mapper.ZFkKamiMapper;

import java.util.List;

@Service(interfaceName = "org.xxpay.core.service.IZFkKamiService", version = "1.0.0", retries = -1)
public class ZFkKamiServiceImpl  implements IZFkKamiService {

    private static final MyLog _log = MyLog.getLog(ZFkKamiServiceImpl.class);
    

    @Autowired
    ZFkKamiMapper zFkKamiMapper;
    
    @Override
    public List<ZFkKami> selectList(ZFkKami zFkKami, int page, int limit) {
        PageHelper.startPage(page, limit);

        page =  (page - 1) * limit;
        limit = limit + page;

        List<ZFkKami> zSlrjAccounts = zFkKamiMapper.selectList(zFkKami, page, limit);
        PageInfo<ZFkKami> pageInfo = new PageInfo<ZFkKami>(zSlrjAccounts);
        return pageInfo.getList();
    }

    @Override
    public int selectListCount(Long parentId) {
        return zFkKamiMapper.selectListCount(parentId);
    }

    @Override
    public ZFkKami selectById(Long id) {
        return zFkKamiMapper.selectById(id);
    }

    @Override
    public ZFkKami selectByCard(String card) {
        return zFkKamiMapper.selectByCard(card);
    }

    @Override
    public int update(ZFkKami zSlrjAccount) {
        return zFkKamiMapper.update(zSlrjAccount);
    }

    @Override
    public int insert(ZFkKami zSlrjAccount) {
        return zFkKamiMapper.insert(zSlrjAccount);
    }

    @Override
    public int deleteByPrimaryKey(Long id) {
        return zFkKamiMapper.deleteByPrimaryKey(id);
    }
}
