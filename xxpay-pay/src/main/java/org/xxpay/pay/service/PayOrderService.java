package org.xxpay.pay.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.domain.AvailablePassageVO;
import org.xxpay.core.common.enumm.AmountType;
import org.xxpay.core.common.util.DateUtil;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.WeightRandom;
import org.xxpay.core.entity.MchPayPassage;
import org.xxpay.core.entity.PayOrder;
import org.xxpay.core.entity.PayPassage;
import org.xxpay.core.entity.PayPassageAccount;
import org.xxpay.pay.mq.BaseNotify4MchPay;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author: dingzhiwei
 * @date: 17/9/9
 * @description:
 */
@Service
public class PayOrderService {

    private static final MyLog _log = MyLog.getLog(PayOrderService.class);

    @Autowired
    private RpcCommonService rpcCommonService;

    @Autowired
    public BaseNotify4MchPay baseNotify4MchPay;

    public PayOrder query(Long mchId, String payOrderId, String mchOrderNo, boolean executeNotify) {
        PayOrder payOrder;
        if (StringUtils.isNotBlank(payOrderId)) {
            payOrder = rpcCommonService.rpcPayOrderService.selectByMchIdAndPayOrderId(mchId, payOrderId);
        } else {
            payOrder = rpcCommonService.rpcPayOrderService.selectByMchIdAndMchOrderNo(mchId, mchOrderNo);
        }
        if (payOrder == null) return null;

        if (executeNotify && (PayConstant.PAY_STATUS_SUCCESS == payOrder.getStatus() || PayConstant.PAY_STATUS_COMPLETE == payOrder.getStatus())) {
            baseNotify4MchPay.doNotify(payOrder, false);
            _log.info("业务查单完成,并再次发送业务支付通知.发送完成");
        }
        return payOrder;
    }

    public PayOrder query(Long mchId, String payOrderId, String mchOrderNo) {
        return query(mchId, payOrderId, mchOrderNo, false);
    }

    /**
     * 获取支付通道子账户
     *
     * @param mchPayPassage
     * @param riskLog
     * @param amountL
     * @return
     */
    public AvailablePassageVO getPayPassageAccount(MchPayPassage mchPayPassage, String riskLog, long amountL) {
        AvailablePassageVO availablePPA = null;
        PayPassageAccount payPassageAccount = null;
        Integer productId = mchPayPassage.getProductId();
        Long mchId = mchPayPassage.getMchId();
        if (mchPayPassage.getIfMode() == 1) {    // 单独接口
            Integer payPassageId = mchPayPassage.getPayPassageId();
            if (mchPayPassage.getPayPassageAccountId() != null && mchPayPassage.getPayPassageAccountId() > 0) {
                payPassageAccount = rpcCommonService.rpcPayPassageAccountService.findById(mchPayPassage.getPayPassageAccountId());
                PayPassage payPassage = rpcCommonService.rpcPayPassageService.findById(payPassageId);
                availablePPA = getAvailablePPA(riskLog, amountL, payPassage, payPassageAccount);
            } else {
                availablePPA = getPPA(riskLog, payPassageId, amountL);
            }
        } else if (mchPayPassage.getIfMode() == 2) {  // 轮询接口
            // 轮询通道参数
            String pollParam = mchPayPassage.getPollParam();
            if (StringUtils.isBlank(pollParam)) {
                return AvailablePassageVO.fail("没有配置轮询通道[productId=" + productId + ",mchId=" + mchId + "]");
            }

            JSONArray array = JSONArray.parseArray(pollParam);
            if (CollectionUtils.isEmpty(array)) {
                return AvailablePassageVO.fail("配置的轮询通道为空[productId=" + productId + ",mchId=" + mchId + "]");
            }
            // [{"payPassageId":1111,"weight":1},{"payPassageId":222,"weight":2}]
            List<Pair> pollPayPassList = new LinkedList<>();
            for (int i = 0; i < array.size(); i++) {
                JSONObject object = array.getJSONObject(i);
                int pid = object.getInteger("payPassageId");
                int weight = object.getInteger("weight");
                Pair pair = new Pair(pid, weight);
                pollPayPassList.add(pair);
            }
            // 递归取得可用通道账户
            availablePPA = recursiveGetPPA(riskLog, pollPayPassList, amountL);
        }

        return availablePPA;
    }

    /**
     * 递归取子账户
     *
     * @param riskLog
     * @param pollPayPassList
     * @param amountL
     * @return
     */
    private AvailablePassageVO recursiveGetPPA(String riskLog, List<Pair> pollPayPassList, long amountL) {
        // 根据权重,随机取得支付通道
        if (CollectionUtils.isEmpty(pollPayPassList)) return null;
        WeightRandom weightRandom = new WeightRandom(pollPayPassList);
        int pid = (Integer) weightRandom.random();
        AvailablePassageVO obj = getPPA(riskLog, pid, amountL);
        if (null != obj) return obj;
        // 如果没有取到有效子账户,则从集合中删除,然后递归取
        final CopyOnWriteArrayList<Pair> cowList = new CopyOnWriteArrayList<>(pollPayPassList);
        for (Pair pair : cowList) {
            if (String.valueOf(pid).equals(String.valueOf(pair.getKey()))) {
                cowList.remove(pair);
            }
        }
        return recursiveGetPPA(riskLog, cowList, amountL);
    }

    /**
     * 根据通道ID取子账户
     *
     * @param riskLog
     * @param payPassageId
     * @param amountL
     * @return
     */
    private AvailablePassageVO getPPA(String riskLog, int payPassageId, long amountL) {
        // 判断支付通道
        PayPassage payPassage = rpcCommonService.rpcPayPassageService.findById(payPassageId);
        if (payPassage == null || payPassage.getStatus() != MchConstant.PUB_YES) {
            return AvailablePassageVO.fail("支付通道不存在或已关闭[payPassageId=" + payPassageId + "]");
        }
        // 得到可用的支付通道子账户
        //List<PayPassageAccount> payPassageAccountList = rpcCommonService.rpcPayPassageAccountService.selectAllByPassageId(payPassageId);
        List<PayPassageAccount> payPassageAccountList = rpcCommonService.rpcPayPassageAccountService.selectAllByPassageId2(payPassageId);
        if (CollectionUtils.isEmpty(payPassageAccountList)) {
            return AvailablePassageVO.fail("该支付通道没有配置可用子账户[payPassageId=" + payPassageId + "]");
        }
        AvailablePassageVO availablePPA = null;
        // 需要根据风控规则得到子账户号
        // 随机打乱子账户顺序
        Collections.shuffle(payPassageAccountList);
        for (PayPassageAccount ppa : payPassageAccountList) {
            availablePPA = getAvailablePPA(riskLog, amountL, payPassage, ppa);
            PayPassageAccount availablePayPassageAccount = null;
            if (availablePPA.getCode() == 1) {
                return availablePPA;
            }

        }
        return availablePPA;
    }

    /**
     * 获取可用子账户(需判断账户状态,及风控)
     *
     * @param riskLog
     * @param amountL
     * @param ppa
     * @return
     */
    private AvailablePassageVO getAvailablePPA(String riskLog, long amountL, PayPassage payPassage, PayPassageAccount ppa) {
        // 判断账号状态为关闭,则返回空
        if (ppa.getStatus() != MchConstant.PUB_YES) {
            return null;
        }
        // 风控继承模式,设置继承的风控信息
        if (ppa.getRiskMode() == 1) {
            ppa.setRiskStatus(payPassage.getRiskStatus());
            ppa.setMaxDayAmount(payPassage.getMaxDayAmount());
            ppa.setMaxEveryAmount(payPassage.getMaxEveryAmount());
            ppa.setMinEveryAmount(payPassage.getMinEveryAmount());
            ppa.setTradeStartTime(payPassage.getTradeStartTime());
            ppa.setTradeEndTime(payPassage.getTradeEndTime());
        }
        // 判断风控状态为关闭,则返回账号对象
        if (ppa.getRiskStatus() == MchConstant.PUB_NO) {   // 风控状态是关闭的
            return AvailablePassageVO.success(ppa);
        }
        // 下面逻辑判断是否触发风控规则,如果触发则返回空
        // 单笔交易金额是否满足
        long maxEveryAmount = ppa.getMaxEveryAmount();
        long minEveryAmount = ppa.getMinEveryAmount();
        String fixAmount = payPassage.getFixAmount();
        List<Long> fixs = Arrays.stream(fixAmount.split(",")).filter(e -> {
            if (StringUtils.isBlank(e)) {
                return false;

            } else {
                return Long.parseLong(e) * 100 == amountL;
            }
        }).map(e -> Long.parseLong(e) * 100).collect(Collectors.toList());
        boolean flag = StringUtils.isNotBlank(fixAmount) ? true : fixs.size() > 0;
        AmountType atype = AmountType.getById(payPassage.getAmountType());

        switch (atype) {
            case FIX_AMOUNT:
                if (flag && !fixs.contains(amountL)) {
                    _log.info("固定金额不匹配");
                    return AvailablePassageVO.fail("固定金额不匹配");
                }
                break;
            case TENS_AMOUNT:
                if (amountL % 1000 != 0) {
                    _log.info("单笔交易金额{}不是10的倍数", amountL / 100);
                    return AvailablePassageVO.fail("单笔交易金额要是10的倍数");
                }
                break;
            case HUNDRED_AMOUNT:
                if (amountL % 10000 != 0) {
                    _log.info("单笔交易金额{}不是100的倍数", amountL / 100);
                    return AvailablePassageVO.fail("单笔交易金额要是100的倍数");
                }
                break;
            case NOTTENS_AMOUNT:
                if (amountL % 1000 == 0) {
                    _log.info("单笔交易金额{}不能是10的倍数", amountL / 100);
                    return AvailablePassageVO.fail("单笔交易金额不能是10的倍数");
                }
                break;
            case CONTINUERS_AMOUNT:
                if ((amountL < minEveryAmount || amountL > maxEveryAmount)) {
                    _log.info("{}单笔交易金额触发风控.payPassageAccountId={},maxEveryAmount={},minEveryAmount={},amount={}", riskLog, ppa.getId(), maxEveryAmount, minEveryAmount, amountL);
                    return AvailablePassageVO.fail("支持最小支付金额:" + minEveryAmount / 100 + "到最大金额:" + maxEveryAmount / 100);
                }
                break;
            default:
                _log.info("{}为其他类型的金额", amountL / 100);

        }
        // 交易时间是否满足
        String ymd = DateUtil.date2Str(new Date(), DateUtil.FORMAT_YYYY_MM_DD);
        String tradeStartTime = ymd + " " + (StringUtils.isBlank(ppa.getTradeStartTime()) ? "00:00:00" : ppa.getTradeStartTime());
        String tradeEndTime = ymd + " " + (StringUtils.isBlank(ppa.getTradeEndTime()) ? "23:59:59" : ppa.getTradeEndTime());

        long startTime = DateUtil.str2date(tradeStartTime).getTime();
        long endTime = DateUtil.str2date(tradeEndTime).getTime();
        long currentTime = System.currentTimeMillis();
        if (currentTime < startTime || currentTime > endTime) {
            _log.info("{}交易时间触发风控.payPassageAccountId={},tradeStartTime={},tradeEndTime={}", riskLog, ppa.getId(), tradeStartTime, tradeEndTime);
            return AvailablePassageVO.fail("请在交易时间内下单");
        }
        // 今日总交易金额是否满足
        long maxDayAmount = ppa.getMaxDayAmount();
        long dayAmount = rpcCommonService.rpcPayOrderService.sumAmount4PayPassageAccount(ppa.getId(),
                DateUtil.str2date(ymd + " 00:00:00"),
                DateUtil.str2date(ymd + " 23:59:59"));
        if (dayAmount > maxDayAmount) {
            _log.info("{}每日交易总额触发风控.payPassageAccountId={},maxDayAmount={},dayAmount={}", riskLog, ppa.getId(), maxDayAmount, dayAmount);
            return AvailablePassageVO.fail("通道暂不可用");
        }
        return AvailablePassageVO.success(ppa);
    }

}
