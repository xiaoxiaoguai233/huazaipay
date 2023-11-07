package org.xxpay.core.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.xxpay.core.entity.AgentInfo;
import org.xxpay.core.entity.Z7881Account;

import java.util.List;

public interface IZ7881AccountService {


    IPage<Z7881Account> listByPage(IPage iPage, Z7881Account z7881Account, JSONObject paramJSON, LambdaQueryWrapper<Z7881Account> wrapper);
}
