package org.xxpay.manage.common.service;

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
    public IMchAccountHistoryService rpcMchAccountHistoryService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchBankAccountService rpcMchBankAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISettRecordService rpcSettRecordService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchSettBatchRecordService rpcMchSettBatchRecordService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayOrderService rpcPayOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ITransOrderService rpcTransOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IRefundOrderService rpcRefundOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAppService rpcMchAppService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchTradeOrderService rpcMchTradeOrderService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchQrCodeService rpcMchQrCodeService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysService rpcSysService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchNotifyService rpcMchNotifyService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IChannelConfigService rpcChannelConfigService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysMessageService rpcSysMessageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ICheckService rpcCheckService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchBillService rpcMchBillService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentInfoService rpcAgentInfoService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAssistantAccountService rpcAssistantAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAssistantInfoService rpcAssistantInfoService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayInterfaceTypeService rpcPayInterfaceTypeService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayInterfaceService rpcPayInterfaceService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentPassageService rpcAgentPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentpayPassageAccountService rpcAgentpayPassageAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentpayPassageService rpcAgentpayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAgentpayPassageService rpcMchAgentpayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchPayPassageService rpcMchPayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayPassageAccountService rpcPayPassageAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayPassageService rpcPayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayProductService rpcPayProductService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IXxPayTransService rpcXxPayTransService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IMchAgentpayService rpcMchAgentpayService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentAccountService rpcAgentAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentAccountHistoryService rpcAgentAccountHistoryService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAssistantAccountHistoryService rpcAssistantAccountHistoryService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IBankCardBinService rpcBankCardBinService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysConfigService rpcSysConfigService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayTypeService rpcPayTypeService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IAgentAgentpayPassageService rpcAgentAgentpayPassageService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ICommonService rpcCommonService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public ISysLogService rpcSysLogService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayCashCollConfigService rpcPayCashCollConfigService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IPayOrderCashCollRecordService rpcPayOrderCashCollRecordService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IWxUserService rpcWxUserService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IXxPayNotifyService rpcXxPayNotifyService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IXxPayMchConfigService rpcPayMchConfigService;

    /**
     * 自定义
     */

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZ7881AccountService rpcZ7881AccountService;

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
    public IZTlbbAccountService rpcZTlbbAccountService;

    @Reference(version = "1.0.0", timeout = 10000, retries = -1)
    public IZZfAccountService rpcZZfAccountService;
}
