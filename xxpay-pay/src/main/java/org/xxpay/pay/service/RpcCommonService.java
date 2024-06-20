package org.xxpay.pay.service;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.xxpay.core.service.*;

/**
 * @author: dingzhiwei
 * @date: 17/9/10
 * @description:
 */
@Service
public class RpcCommonService {

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchInfoService rpcMchInfoService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayOrderService rpcPayOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ITransOrderService rpcTransOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IRefundOrderService rpcRefundOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAppService rpcMchAppService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchNotifyService rpcMchNotifyService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IUserAccountService rpcUserAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchPayPassageService rpcMchPayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayPassageAccountService rpcPayPassageAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentPassageService rpcAgentPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAgentpayPassageService rpcMchAgentpayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentpayPassageAccountService rpcAgentpayPassageAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayPassageService rpcPayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayProductService rpcPayProductService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentpayPassageService rpcAgentpayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAccountHistoryService rpcMchAccountHistoryService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchBankAccountService rpcMchBankAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAccountService rpcMchAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IBankCardBinService rpcBankCardBinService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentAgentpayPassageService rpcAgentAgentpayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentInfoService rpcAgentInfoService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayInterfaceService rpcPayInterfaceService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAgentpayService rpcMchAgentpayService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayCashCollConfigService rpcPayCashCollConfigService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayOrderCashCollRecordService rpcPayOrderCashCollRecordService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IWxUserService rpcWxUserService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ICashierConfigService rpcCashierConfigService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IXxPayMchConfigService rpcPayMchConfigService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysService rpcSysService;

    /**
     * 自定义收款Dubbo
     */
    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZHshAccountService rpcZHshAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZMthAccountService rpcZMthAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZHxtAccountService rpcZHxtAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZSlrjAccountService rpcZSlrjAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZFkKamiService rpcZFkKamiService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZFkKamiShopService rpcZFkKamiShopService;


    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAssistantInfoService rpcAsstInfoService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZTlbbAccountService rpcZTlbbAccountService;
}
