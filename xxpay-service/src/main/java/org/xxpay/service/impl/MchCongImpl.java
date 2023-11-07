package org.xxpay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.xxpay.core.entity.MchConfig;
import org.xxpay.core.entity.MchConfigExample;
import org.xxpay.core.entity.WxUser;
import org.xxpay.core.entity.WxUserExample;
import org.xxpay.core.service.IWxUserService;
import org.xxpay.core.service.IXxPayMchConfigService;
import org.xxpay.service.dao.mapper.MchConfigMapper;
import org.xxpay.service.dao.mapper.WxUserMapper;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author: dingzhiwei
 * @date: 17/9/8
 * @description:
 */
@Service(interfaceName = "org.xxpay.core.service.IXxPayMchConfigService", version = "1.0.0", retries = -1)
public class MchCongImpl implements IXxPayMchConfigService {

    @Autowired
    MchConfigMapper mchConfigMapper;

    @Override
    public void add(MchConfig mchConfig) {
        mchConfigMapper.insertSelective(mchConfig);
    }

    @Override
    public void update(MchConfig mchConfig) {
        MchConfigExample example = new MchConfigExample();
        MchConfigExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(mchConfig.getId());
        criteria.andConfigNameEqualTo(mchConfig.getConfigName());
        mchConfigMapper.updateByExampleSelective(mchConfig, example);
    }

    @Override
    public MchConfig get(Long mchId, String configName) {
        MchConfigExample example = new MchConfigExample();
        MchConfigExample.Criteria criteria = example.createCriteria();
        if (mchId != null) {
            criteria.andMchIdEqualTo(mchId);
        } else {
            criteria.andMchIdIsNull();
        }
        criteria.andConfigNameEqualTo(configName);
        List<MchConfig> mchConfigs = mchConfigMapper.selectByExample(example);
        return mchConfigs.isEmpty() ? null : mchConfigs.get(0);
    }

    @Override
    public List<MchConfig> list(Long mchId,Byte confitType) {
        MchConfigExample example = new MchConfigExample();
        MchConfigExample.Criteria criteria = example.createCriteria();
        if("null".equalsIgnoreCase(String.valueOf(mchId))){
            criteria.andIdIsNotNull();
        } else {
            criteria.andMchIdEqualTo(mchId);
        }
        if("null".equalsIgnoreCase(String.valueOf(confitType))){
            criteria.andConfigTypeIsNotNull();
        } else {
            criteria.andConfigTypeEqualTo(confitType);
        }
        List<MchConfig> queryList = mchConfigMapper.selectByExample(example);
        List<MchConfig> resList = new ArrayList<>();
        Set mchSet = new HashSet<>();
        Date maxCreateTime = null;
        Date maxUpdateTime = null;
        for(MchConfig vo:queryList){
            mchSet.add(vo.getMchId());
        }
        Iterator it= mchSet.iterator();
        while(it.hasNext()){
            MchConfig resObj = new MchConfig();
            Object idObj = it.next();
            if("null".equals(String.valueOf(idObj))){
                resObj.setMchId(null);
                for(MchConfig vo:queryList){
                   if(vo.getMchId()==null) {
                       maxCreateTime = vo.getCreateTime();
                       maxUpdateTime = vo.getCreateTime();
                       break;
                   };
                }
                for(MchConfig vo:queryList){
                    if(vo.getMchId()==null) {
                        maxCreateTime = maxCreateTime.getTime()< vo.getCreateTime().getTime()?vo.getCreateTime():maxCreateTime;
                        maxUpdateTime = maxUpdateTime.getTime()< vo.getCreateTime().getTime()?vo.getUpdateTime():maxUpdateTime;
                    }
                }
                resObj.setConfigType((byte)0);
                resObj.setCreateTime(maxCreateTime);
                resObj.setUpdateTime(maxUpdateTime);
            }else{
                for(MchConfig vo:queryList){
                    if(String.valueOf(idObj).equals(vo.getMchId())){
                        maxCreateTime = vo.getCreateTime();
                        maxUpdateTime = vo.getCreateTime();
                        break;
                    }
                }
                for(MchConfig vo:queryList){
                    if(String.valueOf(idObj).equals(vo.getMchId())) {
                        maxCreateTime = maxCreateTime.getTime()< vo.getCreateTime().getTime()? vo.getCreateTime():maxCreateTime;
                        maxUpdateTime = maxUpdateTime.getTime()< vo.getCreateTime().getTime()?vo.getUpdateTime():maxUpdateTime;
                    }
                }
                resObj.setMchId(new BigDecimal(String.valueOf(idObj)).longValue());
                resObj.setConfigType((byte)1);
                resObj.setCreateTime(maxCreateTime);
                resObj.setUpdateTime(maxUpdateTime);
            }
            resList.add(resObj);
        }
        return resList;
    }

    @Override
    public MchConfig getGlobal(String configName) {
        MchConfigExample example = new MchConfigExample();
        MchConfigExample.Criteria criteria = example.createCriteria();
        criteria.andConfigNameEqualTo(configName);
        List<MchConfig> mchConfigList = mchConfigMapper.selectByExample(example);
        if(mchConfigList.isEmpty()){
            return null;
        }
        return mchConfigList.get(0);
    }

    @Override
    public MchConfig getById(Long Id) {
        MchConfigExample example = new MchConfigExample();
        MchConfigExample.Criteria criteria = example.createCriteria();
        if (Id != null) criteria.andIdEqualTo(Id);
        List<MchConfig> mchConfigs = mchConfigMapper.selectByExample(example);
        return mchConfigs.isEmpty() ? null : mchConfigs.get(0);
    }

    @Override
    public List<MchConfig> getByMchId(Long mchId){
        MchConfigExample example = new MchConfigExample();
        MchConfigExample.Criteria criteria = example.createCriteria();
        if("null".equalsIgnoreCase(String.valueOf(mchId))){
            criteria.andMchIdIsNull();
        } else {
            criteria.andMchIdEqualTo(mchId);
        }
        return mchConfigMapper.selectByExample(example);
    }

}
