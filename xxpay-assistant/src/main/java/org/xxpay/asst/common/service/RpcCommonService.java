package org.xxpay.asst.common.service;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.xxpay.core.service.*;

/**
 * @author: dingzhiwei
 * @date: 17/12/05
 * @description:
 */
@Service
public class RpcCommonService {

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchInfoService rpcMchInfoService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAccountService rpcMchAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysMessageService rpcSysMessageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysService rpcSysService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAssistantInfoService rpcAssistantInfoService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAssistantAccountService rpcAssistantAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAssistantAccountHistoryService rpcAssistantAccountHistoryService;


    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayProductService rpcPayProductService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchPayPassageService rpcMchPayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayOrderService rpcPayOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISettRecordService rpcSettRecordService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysLogService rpcSysLogService;


    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZFkKamiService rpcZFkKamiService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZSlrjAccountService rpcZSlrjAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZTlbbAccountService rpcZTlbbAccountService;
}
