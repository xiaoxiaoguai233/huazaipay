package org.xxpay.pay.ctrl.payment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.PayConstant;
import org.xxpay.core.common.constant.PayEnum;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.enumm.AmountType;
import org.xxpay.core.common.util.*;
import org.xxpay.core.entity.*;
import org.xxpay.pay.ctrl.common.BaseController;
import org.xxpay.pay.service.PayOrderService;
import org.xxpay.pay.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 支付收银台
 * @date 2018-06-07
 * @Copyright: www.xxpay.org
 */
@Controller
@RequestMapping("/api/cashier/common/")
public class CashierWebController extends BaseController {

    private static final MyLog _log = MyLog.getLog(CashierWebController.class);

    @Autowired
    private RpcCommonService rpcCommonService;

    @Autowired
    private PayOrderService payOrderService;

    @RequestMapping(value = "/getLimit")
    @ResponseBody
    public ResponseEntity<?> getLimit(HttpServletRequest request) {
        MchConfig cashierLimit;
        String mchId = request.getParameter("mchId");
        if (StringUtils.isNotBlank(mchId)) {
            cashierLimit = rpcCommonService.rpcPayMchConfigService.get(Long.parseLong(mchId), "cashierLimit");
        } else {
            cashierLimit = rpcCommonService.rpcPayMchConfigService.getGlobal("cashierLimit");
        }
        String configValue = cashierLimit.getConfigValue();
        return ResponseEntity.ok(JSONObject.parseObject(configValue));

    }

    @RequestMapping(value = "/build")
    @ResponseBody
    public String buildPaymentUrl(HttpServletRequest request) {
        _log.info("###### 开始接收商户生成web收银台地址请求 ######");
        String logPrefix = "【生成收银台地址】";
        try {
            JSONObject po = getJsonParam(request);
            _log.info("{}请求参数:{}", logPrefix, po);
            String mchId = po.getString("mchId");                // 商户ID
            String appId = po.getString("appId");               // 应用ID
            String subMchId = po.getString("subMchId");               // 玩家账号
            String sign = po.getString("sign");                    // 签名数据
            String createTime = po.getString("ctime");                    // 创建时间

            String errorMessage = "";
            if (StringUtils.isBlank(sign)) {
                errorMessage += "请求参数[sign]不能为空.";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
            }
            Long mchIdL;
            if (StringUtils.isBlank(mchId) || !NumberUtils.isDigits(mchId)) {
                errorMessage += "请求参数[mchId]不能为空且为数值类型.";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));

            }
            mchIdL = Long.parseLong(mchId);

            if (StringUtils.isBlank(subMchId)) {
                errorMessage += "请求参数[subMchId]不能为空.";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
            }

            if (StringUtils.isBlank(createTime)) {
                errorMessage += "请求参数[createTime]不能为空.";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
            }

            // 查询商户信息
            MchInfo mchInfo = rpcCommonService.rpcMchInfoService.findByMchId(mchIdL);
            if (mchInfo == null) {
                errorMessage += "商户不存在[mchId=" + mchId + "].";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
            }
            if (mchInfo.getStatus() != MchConstant.PUB_YES) {
                errorMessage += "商户状态不可用[mchId=" + mchId + "].";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
            }
            // 查询应用信息
            if (StringUtils.isNotBlank(appId)) {
                MchApp mchApp = rpcCommonService.rpcMchAppService.findByMchIdAndAppId(mchIdL, appId);
                if (mchApp == null) {
                    errorMessage += "应用不存在[appId=" + appId + "].";
                    _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                    return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
                }
                if (mchApp.getStatus() != MchConstant.PUB_YES) {
                    errorMessage += "应用状态不可用[appId=" + appId + "].";
                    _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                    return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
                }
            }

            String key = mchInfo.getPrivateKey();
            if (StringUtils.isBlank(key)) {
                errorMessage += "商户私钥为空,请配置商户私钥[mchId=" + mchId + "].";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
            }

            // 验证签名数据
            boolean verifyFlag = XXPayUtil.verifyPaySign(po, key);
            if (!verifyFlag) {
                errorMessage = "验证签名失败.";
                _log.info("{}参数校验不通过:{}", logPrefix, errorMessage);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, errorMessage, null, PayEnum.ERR_0014.getCode(), errorMessage));
            }

            // 做签名(和收银台验签保持一致)
//            Map desMap = new HashMap<>();
//            desMap.put("mchId", mchId);
//            if (appId != null) desMap.put("appId", appId);
//            sign = PayDigestUtil.getSign(desMap, key);
            String payUrl;
            payUrl = String.format("%s/cashier/common/pc?mchId=%s&appId=%s&subMchId=%s&ctime=%s&sign=%s",
                    super.payUrl, mchId, appId, subMchId, createTime, sign);
            String mobilePayUrl = String.format("%s/cashier/common/mobile?mchId=%s&appId=%s&subMchId=%s&ctime=%s&sign=%s",
                    super.payUrl, mchId, appId, subMchId, createTime, sign);

            JSONObject retObj = new JSONObject();
            retObj.put(PayConstant.RETURN_PARAM_RETCODE, PayConstant.RETURN_VALUE_SUCCESS);
            retObj.put("payUrl", payUrl);
            retObj.put("mobilePayUrl", mobilePayUrl);
            return XXPayUtil.makeRetData(retObj, key);

        } catch (Exception e) {
            _log.error(e, "");
            return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "支付中心系统异常", null, PayEnum.ERR_0010.getCode(), "请联系技术人员查看"));
        }
    }

    /**
     * 打开收银台页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/pc")
    public String pc(HttpServletRequest request, ModelMap model) {


        String mchIdStr = request.getParameter("mchId");
        String appId = request.getParameter("appId");
        String subMchId = request.getParameter("subMchId");
        String ctime = request.getParameter("ctime");
        Long mchId = Long.parseLong(mchIdStr);
        String sign = request.getParameter("sign");
        // 查询商户信息
        MchInfo mchInfo = rpcCommonService.rpcMchInfoService.findByMchId(mchId);
        if (mchInfo == null) {
            model.put("errMsg", RetEnum.RET_MCH_CONFIG_NOT_EXIST);
            return PAGE_COMMON_PC_ERROR;
        }
        if (mchInfo.getStatus() != MchConstant.PUB_YES) {
            model.put("errMsg", RetEnum.RET_MCH_STATUS_CLOSE);
            return PAGE_COMMON_PC_ERROR;
        }

        // 查询应用信息
        if (StringUtils.isNotBlank(appId)) {
            MchApp mchApp = rpcCommonService.rpcMchAppService.findByMchIdAndAppId(mchId, appId);
            if (mchApp == null) {
                model.put("errMsg", RetEnum.RET_MCH_CONFIG_NOT_EXIST);
                return PAGE_COMMON_PC_ERROR;
            }

            if (mchApp.getStatus() != MchConstant.PUB_YES) {
                model.put("errMsg", RetEnum.RET_MCH_STATUS_CLOSE);
                return PAGE_COMMON_PC_ERROR;
            }
        }

        String key = mchInfo.getPrivateKey();
        if (StringUtils.isBlank(key)) {
            model.put("errMsg", buildRetMap(11902, "商户没有配置私钥"));
            return PAGE_COMMON_PC_ERROR;
        }
        Map desMap = new HashMap<>();
        desMap.put("mchId", mchId);
        desMap.put("appId", appId);
        desMap.put("subMchId", subMchId);
        desMap.put("ctime", ctime);
        String validateSign = PayDigestUtil.getSign(desMap, key);
        if (!sign.equals(validateSign)) {
            model.put("errMsg", buildRetMap(11903, "签名错误"));
            return PAGE_COMMON_PC_ERROR;
        }
        List<MchPayPassage> mchPayPassages = rpcCommonService.rpcMchPayPassageService.selectAllByMchId(mchId).stream().filter(e -> e.getStatus() == 1).collect(Collectors.toList());
        HashMap<Integer, MchPayPassage> payPassageHashMap = new HashMap<>();
        mchPayPassages.stream().forEach(e -> {
            payPassageHashMap.put(e.getProductId(), e);
        });

//        ArrayList<Integer> productIds = mchPayPassages.stream().map(e -> {
//            payPassageHashMap.put(e.getProductId(), e);
//            return e.getProductId();
//        }).collect(Collectors.toCollection(ArrayList::new));
        List<Integer> productIds = checkPassage(mchPayPassages);
        if (CollectionUtils.isEmpty(productIds)) {
            model.put("errMsg", buildRetMap(11904, "支付产品不存在,producIds=" + productIds));
            return PAGE_COMMON_PC_ERROR;
        }
        List<PayProduct> payProductList = rpcCommonService.rpcPayProductService.selectAll(productIds);
        HashMap<String, SupportAmount> amountMsg = new HashMap<>();

        List<String> payTypeList = payProductList.stream().map(e -> {
            getAmountRange(payPassageHashMap, amountMsg, e);
            return e.getPayType();
        }).distinct().collect(Collectors.toList());
        if (amountMsg.size() > 0) {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            toJsonStr(stringStringHashMap, amountMsg);
            model.put("amountMsg", JSONObject.toJSONString(amountMsg));
        }
        payTypeList.sort(Comparator.reverseOrder());
        model.put("payType", payTypeList);

        // 设置支付页面所需参数
        model.put("mchId", mchId);
        model.put("mchName", mchInfo.getName());
        model.put("subMchId", subMchId);
        model.put("ctime", getTimeFormate(Long.parseLong(ctime)));
        model.put("appId", appId);


        return "cashier/web";
    }

    private List<Integer> checkPassage(List<MchPayPassage> mchPayPassages) {
        HashMap<Integer, ArrayList<Integer>> product2Passage = new HashMap<>();
        ArrayList<Integer> ids = new ArrayList<>();
        mchPayPassages.stream().forEach(e -> {
            if (e.getIfMode() == 1) {
                ids.add(e.getPayPassageId());
                ArrayList<Integer> integers = new ArrayList<>();
                integers.add(e.getPayPassageId());
                product2Passage.put(e.getProductId(), integers);
            } else {
                JSONArray objects = JSONArray.parseArray(e.getPollParam());
                ArrayList<Integer> integers = new ArrayList<>();
                if (product2Passage.get(e.getProductId()) != null) {
                    integers = product2Passage.get(e.getProductId());
                }
                for (int i = 0; i < objects.size(); i++) {
                    Integer payPassageId = objects.getJSONObject(i).getInteger("payPassageId");
                    integers.add(payPassageId);
                    ids.add(payPassageId);
                    product2Passage.put(e.getProductId(), integers);
                }
            }
        });
        _log.info("+++++++++" + JSONObject.toJSONString(product2Passage));
        Byte status = 0;
        List<PayPassage> payPassages = rpcCommonService.rpcPayPassageService.selectAllByIdsAndTimeAvailable(ids, status);
        if (payPassages.isEmpty() || payPassages.size() == 0) {
            List<Integer> collect = product2Passage.keySet().stream().collect(Collectors.toList());
        } else {
            List<Integer> passegeIds = payPassages.stream().map(e -> e.getId()).collect(Collectors.toList());

            for (Integer id : product2Passage.keySet()) {
                product2Passage.get(id).removeAll(passegeIds);
            }

        }
        Iterator<Map.Entry<Integer, ArrayList<Integer>>> iterator = product2Passage.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ArrayList<Integer>> next = iterator.next();
            if (next.getValue().size() == 0) {
                iterator.remove();
            }
        }
        _log.info("=========" + JSONObject.toJSONString(product2Passage));
        List<Integer> collect = product2Passage.keySet().stream().collect(Collectors.toList());
        return collect;
    }

    /**
     * 打开PC收银台页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/mobile")
    public String wap(HttpServletRequest request, ModelMap model) {


        String mchIdStr = request.getParameter("mchId");
        String appId = request.getParameter("appId");
        String subMchId = request.getParameter("subMchId");
        String ctime = request.getParameter("ctime");
        Long mchId = Long.parseLong(mchIdStr);
        String sign = request.getParameter("sign");
        // 查询商户信息
        MchInfo mchInfo = rpcCommonService.rpcMchInfoService.findByMchId(mchId);
        if (mchInfo == null) {
            model.put("errMsg", RetEnum.RET_MCH_CONFIG_NOT_EXIST);
            return PAGE_COMMON_PC_ERROR;
        }
        if (mchInfo.getStatus() != MchConstant.PUB_YES) {
            model.put("errMsg", RetEnum.RET_MCH_STATUS_CLOSE);
            return PAGE_COMMON_PC_ERROR;
        }

        // 查询应用信息
        if (StringUtils.isNotBlank(appId)) {
            MchApp mchApp = rpcCommonService.rpcMchAppService.findByMchIdAndAppId(mchId, appId);
            if (mchApp == null) {
                model.put("errMsg", RetEnum.RET_MCH_CONFIG_NOT_EXIST);
                return PAGE_COMMON_PC_ERROR;
            }

            if (mchApp.getStatus() != MchConstant.PUB_YES) {
                model.put("errMsg", RetEnum.RET_MCH_STATUS_CLOSE);
                return PAGE_COMMON_PC_ERROR;
            }
        }

        String key = mchInfo.getPrivateKey();
        if (StringUtils.isBlank(key)) {
            model.put("errMsg", buildRetMap(11902, "商户没有配置私钥"));
            return PAGE_COMMON_PC_ERROR;
        }
        Map desMap = new HashMap<>();
        desMap.put("mchId", mchId);
        desMap.put("appId", appId);
        desMap.put("subMchId", subMchId);
        desMap.put("ctime", ctime);
        String validateSign = PayDigestUtil.getSign(desMap, key);
        if (!sign.equals(validateSign)) {
            model.put("errMsg", buildRetMap(11903, "签名错误"));
            return PAGE_COMMON_PC_ERROR;
        }
        List<MchPayPassage> mchPayPassages = rpcCommonService.rpcMchPayPassageService.selectAllByMchId(mchId).stream().filter(e -> e.getStatus() == 1).collect(Collectors.toList());
        HashMap<Integer, MchPayPassage> payPassageHashMap = new HashMap<>();
        mchPayPassages.stream().forEach(e -> {
            payPassageHashMap.put(e.getProductId(), e);
        });
//        ArrayList<Integer> productIds = mchPayPassages.stream().map(e -> {
//            payPassageHashMap.put(e.getProductId(), e);
//            return e.getProductId();
//        }).collect(Collectors.toCollection(ArrayList::new));
        List<Integer> productIds = checkPassage(mchPayPassages);
        if (CollectionUtils.isEmpty(productIds)) {
            model.put("errMsg", buildRetMap(11904, "支付产品不存在,producIds=" + productIds));
            return PAGE_COMMON_PC_ERROR;
        }
        List<PayProduct> payProductList = rpcCommonService.rpcPayProductService.selectAll(productIds);
        HashMap<String, SupportAmount> amountMsg = new HashMap<>();

        List<String> payTypeList = payProductList.stream().map(e -> {
            getAmountRange(payPassageHashMap, amountMsg, e);
            return e.getPayType();
        }).distinct().collect(Collectors.toList());
        if (amountMsg.size() > 0) {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
//            toJsonStr(stringStringHashMap, amountMsg);
            model.put("amountMsg", JSONObject.toJSONString(amountMsg));
        }
        payTypeList.sort(Comparator.reverseOrder());
        model.put("payType", payTypeList);

        // 设置支付页面所需参数
        model.put("mchId", mchId);
        model.put("mchName", mchInfo.getName());
        model.put("subMchId", subMchId);
        model.put("ctime", getTimeFormate(Long.parseLong(ctime)));
        model.put("appId", appId);

        return "cashier/wap";
    }

    private void getAmountRange(HashMap<Integer, MchPayPassage> payPassageHashMap, HashMap<String, SupportAmount> amountMsg, PayProduct e) {
        MchPayPassage mchPayPassage = payPassageHashMap.get(e.getId());
        SupportAmount avaliablePass = new SupportAmount();
        if (mchPayPassage.getIfMode() == 1) {
            avaliablePass = sigal(mchPayPassage);
        } else {
            avaliablePass = getAvaliablePass(mchPayPassage);
        }
        if (null != avaliablePass) {
            if (amountMsg.get(e.getPayType()) == null) {
                amountMsg.put(e.getPayType(), avaliablePass);
            } else {
                SupportAmount supportAmount = (SupportAmount) amountMsg.get(e.getPayType());
                if (!avaliablePass.getFixAmount().isEmpty()) {
                    List<Long> fixAmount = supportAmount.getFixAmount();
                    fixAmount.addAll(avaliablePass.getFixAmount());
                    supportAmount.setFixAmount(fixAmount);
                    return;
                }
                if (supportAmount.getMax() < avaliablePass.getMax()) {
                    supportAmount.setMax(avaliablePass.getMax());
                }
                if (supportAmount.getMin() > avaliablePass.getMin()) {
                    supportAmount.setMin(avaliablePass.getMin());
                }
                amountMsg.put(e.getPayType(), supportAmount);
            }
        }
    }

    private void toJsonStr(HashMap<String, String> res, HashMap<String, SupportAmount> amountMsg) {
        for (String s : amountMsg.keySet()) {
            res.put(s, JSONObject.toJSONString(amountMsg.get(s)));
        }
    }

    /**
     * 打开PC订单完成页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping("/complete")
    public String pcComplete(HttpServletRequest request, ModelMap model) {

        JSONObject param = getJsonParam(request);
        Long mchId = getLongRequired(param, "mchId");
        String payOrderId = getString(param, "payOrderId");
        // 校验参数

        MchInfo mchInfo = rpcCommonService.rpcMchInfoService.findByMchId(mchId);
        if (mchInfo == null) {
            model.put("errMsg", RetEnum.RET_SERVICE_MCH_NOT_EXIST);
            return PAGE_COMMON_PC_ERROR;
        }
        if (MchConstant.PUB_NO == mchInfo.getStatus()) {
            model.put("errMsg", RetEnum.RET_MCH_STATUS_STOP);
            return PAGE_COMMON_PC_ERROR;
        }

        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByMchIdAndPayOrderId(mchId, payOrderId);
        // 判断订单是否存在
        if (payOrder == null) {
            model.put("errMsg", RetEnum.RET_MCH_TRADE_ORDER_NOT_EXIST);
            return PAGE_COMMON_PC_ERROR;
        }

        PayProduct payProduct = rpcCommonService.rpcPayProductService.findById(payOrder.getProductId());

        // 设置页面所需参数
        model.put("subject", payOrder.getSubject());
        model.put("payType", payProduct == null ? "" : payProduct.getPayType());
        model.put("amountStr", AmountUtil.convertCent2Dollar(payOrder.getAmount() + ""));
        model.put("mchOrderNo", payOrder.getMchOrderNo());
        model.put("mchId", mchId);
        model.put("mchName", mchInfo.getName());
        model.put("status", payOrder.getStatus());
        return "cashier/pc_complete";
    }

    /**
     * PC支付
     *
     * @param request
     * @return
     */
    @RequestMapping("/pay")
    @ResponseBody
    public ResponseEntity<?> pcPay(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String mchIdStr = request.getParameter("mchId");
        String appId = request.getParameter("appId");
        String amount = request.getParameter("amount");
        String payType = request.getParameter("payType");
        String ctime = request.getParameter("ctime");
        String subMchId = request.getParameter("subMchId");
        String orderId = request.getParameter("orderId");
        String clientIp = request.getParameter("clientIp");

        if (!NumberUtils.isDigits(mchIdStr) || !NumberUtils.isDigits(amount)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_PARAM_ERROR));
        }

        Long mchId = Long.parseLong(mchIdStr);
        Long amountL = Long.parseLong(amount) * 100L;

        // 查询商户信息
        MchInfo mchInfo = rpcCommonService.rpcMchInfoService.findByMchId(mchId);
        if (mchInfo == null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_CONFIG_NOT_EXIST));
        }
        if (mchInfo.getStatus() != MchConstant.PUB_YES) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_STATUS_CLOSE));
        }

        // 查询应用信息
        if (StringUtils.isNotBlank(appId)) {
            MchApp mchApp = rpcCommonService.rpcMchAppService.findByMchIdAndAppId(mchId, appId);
            if (mchApp == null) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_CONFIG_NOT_EXIST));
            }
            if (mchApp.getStatus() != MchConstant.PUB_YES) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_STATUS_CLOSE));
            }
        }

        PayOrder queryPayOrder = new PayOrder();
//        生成一个客户订单号
        if(StringUtils.isBlank(orderId)) {
             orderId = DateUtils.getCurrentTimeStrDefault();
        }
        queryPayOrder.setMchOrderNo(orderId);
        List<Integer> collect = rpcCommonService.rpcMchPayPassageService.selectAllByMchId(mchId).stream().filter(e -> e.getStatus() == 1).map(e -> e.getProductId()).collect(Collectors.toList());
        List<PayProduct> payProducts = rpcCommonService.rpcPayProductService.selectAll(collect).stream().filter(e -> e.getStatus() == 1 && e.getPayType().equals(payType.toString())).collect(Collectors.toList());
        PayProduct payProduct = null;
        if (payProducts.size() > 0) {
            payProduct = payProducts.get(RandomUtils.nextInt(0, payProducts.size()));
        }
        // 创建支付订单
        try {

            Integer productId = payProduct.getId();
            CashierConfig cashierConfig = rpcCommonService.rpcCashierConfigService.get(mchId);//获取商户收银台配置
            if (cashierConfig == null && StringUtils.isBlank(cashierConfig.getCallbackUrl())) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_SERVICE_CASHIER_CONFIG_NOT_EXIST));
            } else if (cashierConfig.getStatus() == 0) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_SERVICE_CASHIER_CONFIG_NOT_OPEN));
            }

            Map resMap = createPayOrder(mchInfo, appId, orderId, productId.toString(), amountL, "pay", "body", cashierConfig.getCallbackUrl(), subMchId, "", cashierConfig.getReturnUrl(), clientIp);
            if (resMap != null && ("0121").equals(resMap.get("errCode"))) {
                return ResponseEntity.ok(XxPayResponse.buildSuccess(JSONObject.toJSON(resMap)));
            }
            String payOrderId = resMap.get("payOrderId").toString();
            jsonObject.put("productId", productId);
            jsonObject.put("payUrl", resMap.get("payUrl"));
            jsonObject.put("payAction", resMap.get("payAction"));
            jsonObject.put("payParams", resMap.get("payParams"));
            jsonObject.put("payOrderId", payOrderId);
            return ResponseEntity.ok(XxPayResponse.buildSuccess(jsonObject));
        } catch (Exception e) {
            _log.error(e, "创建订单失败");
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_CREATE_PAY_ORDER_FAIL));
        }
    }

    /**
     * 交易查询
     *
     * @param request
     * @return
     */
    @RequestMapping("/query")
    @ResponseBody
    public ResponseEntity<?> queryPay(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String payOrderId = request.getParameter("payOrderId");
        if (StringUtils.isBlank(payOrderId)) return ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_PARAM_ERROR));
        PayOrder payOrder = rpcCommonService.rpcPayOrderService.findByPayOrderId(payOrderId);
        if (payOrder == null) return ResponseEntity.ok(BizResponse.build(RetEnum.RET_COMM_RECORD_NOT_EXIST));
        byte status = payOrder.getStatus();
        jsonObject.put("status", status);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(jsonObject));
    }

    /**
     * 创建支付订单
     *
     * @param mchInfo
     * @param appId
     * @return
     */
    private Map createPayOrder(MchInfo mchInfo, String appId, String mchOrderNo, String productId, long amount, String subject, String body, String notifyUrl, String subMchId, String extra, String returnUrl,String clientIp) {
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchInfo.getMchId());                          // 商户ID
        paramMap.put("appId", appId);                                       // 应用ID
        paramMap.put("mchOrderNo", mchOrderNo);                            // 商户交易单号
        paramMap.put("productId", productId);                               // 支付产品ID
        paramMap.put("amount", amount);                                     // 支付金额,单位分
        paramMap.put("currency", "cny");                                    // 币种, cny-人民币
        String ip = StringUtils.isBlank(clientIp)?IPUtility.getLocalIP():clientIp;
        paramMap.put("clientIp", ip);                   // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                                      // 设备
        paramMap.put("subject", subject);
        paramMap.put("body", body);
        paramMap.put("notifyUrl", notifyUrl);                               // 回调URL
        paramMap.put("param1", "");                                         // 扩展参数1
        paramMap.put("param2", "");                                         // 扩展参数2
        paramMap.put("payPassageAccountId", null);           // 子账户
        paramMap.put("extra", extra);
        paramMap.put("returnUrl", returnUrl);
        paramMap.put("subMchId", subMchId);                            //商户子账号

        String reqSign = PayDigestUtil.getSign(paramMap, mchInfo.getPrivateKey());
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        _log.info("xxpay_req:{}", reqData);
        String url = payUrl + "/pay/create_order?";
        String result = XXPayUtil.call4Post(url + reqData);
        _log.info("xxpay_res:{}", result);
        Map retMap = JSON.parseObject(result);
        if (XXPayUtil.isSuccess(retMap)) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, mchInfo.getPrivateKey(), "sign");
            String retSign = (String) retMap.get("sign");
            //if(checkSign.equals(retSign)) return retMap;
            //_log.info("验签失败:retSign={},checkSign={}", retSign, checkSign);
            return retMap;
        }
        return null;
    }

    Map buildRetMap(int code, String message) {
        Map retMap = new HashMap<>();
        retMap.put("code", code);
        retMap.put("message", message);
        return retMap;
    }

    private String getTimeFormate(long time1) {
        String result1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time1 * 1000));
        return result1;

    }

    private SupportAmount getAvaliablePass(MchPayPassage mchPayPassage) {
        Long max = 0l;
        Long min = 9999999l;
        HashMap<Integer, AmountMaxMinType> integerHashMapHashMap = new HashMap<>();
        SupportAmount supportAmount = new SupportAmount();
        String pollParam = mchPayPassage.getPollParam();
        if (StringUtils.isBlank(pollParam)) {
            return supportAmount;
        }
        JSONArray array = JSONArray.parseArray(pollParam);
        if (CollectionUtils.isEmpty(array)) {
            return supportAmount;
        }
        List<Long> collect = new ArrayList<>();
        AmountMaxMinType amountMaxMinType = new AmountMaxMinType();
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            int pid = object.getInteger("payPassageId");
            PayPassage passage = rpcCommonService.rpcPayPassageService.findById(pid);
            if (null == passage || passage.getRiskStatus() == 0)
                continue;
            AmountMaxMinType longs = integerHashMapHashMap.get(passage.getAmountType());
            if (longs == null) {
                amountMaxMinType = new AmountMaxMinType();
                amountMaxMinType.setMin(passage.getMinEveryAmount() / 100);
                amountMaxMinType.setMax(passage.getMaxEveryAmount() / 100);
                amountMaxMinType.setType(passage.getAmountType().toString());
                integerHashMapHashMap.put(passage.getAmountType(), amountMaxMinType);
            } else {
                if (longs.getMin() > passage.getMinEveryAmount() / 100) {
                    longs.setMin(passage.getMinEveryAmount() / 100);
                }
                if (longs.getMax() < passage.getMaxEveryAmount() / 100) {
                    longs.setMax(passage.getMaxEveryAmount() / 100);
                }
                integerHashMapHashMap.put(passage.getAmountType(), longs);

            }
            String fixAmount = passage.getFixAmount();
            if (StringUtils.isNotBlank(fixAmount)) {
                collect.addAll(Arrays.stream(fixAmount.split(",")).map(e -> Long.parseLong(e)).collect(Collectors.toList()));
                supportAmount.setFixAmount(collect);
            }
            if (max < passage.getMaxEveryAmount()) {
                max = passage.getMaxEveryAmount();
                supportAmount.setMax(max / 100);
            }
            if (min > passage.getMinEveryAmount()) {
                min = passage.getMinEveryAmount();
                supportAmount.setMin(min / 100);
            }
        }
        AmountMaxMinType continusRange = integerHashMapHashMap.get(AmountType.CONTINUERS_AMOUNT.getId());
        AmountMaxMinType notTenRange = integerHashMapHashMap.get(AmountType.NOTTENS_AMOUNT.getId());
        AmountMaxMinType TenRange = integerHashMapHashMap.get(AmountType.TENS_AMOUNT.getId());
        AmountMaxMinType hundredRange = integerHashMapHashMap.get(AmountType.HUNDRED_AMOUNT.getId());
        List<AmountMaxMinType> res = new ArrayList<>();
        AmountMaxMinType a = null;
        AmountMaxMinType b = null;
        AmountMaxMinType c = null;
        AmountMaxMinType d = null;
        if (continusRange != null) {
            a = update(continusRange, notTenRange);
            b = update(continusRange, TenRange);
            c = update(continusRange, hundredRange);
        }
        if (TenRange != null) {
            d = update(TenRange, hundredRange);
        }
        long count = res.stream().filter(e -> e.getType() == AmountType.CONTINUERS_AMOUNT.getId().toString()).count();
        long notTen = res.stream().filter(e -> e.getType() == AmountType.NOTTENS_AMOUNT.getId().toString()).count();
        long tens = res.stream().filter(e -> e.getType() == AmountType.TENS_AMOUNT.getId().toString()).count();
        long hundreds = res.stream().filter(e -> e.getType() == AmountType.HUNDRED_AMOUNT.getId().toString()).count();
        if (a != null) {
            res.add(a);
        }
        if (b != null) {
            res.add(b);
        }
        if (c != null) {
            res.add(c);
        }
        if (d != null) {
            res.add(d);
        }
        if (count == 0 && continusRange != null) {
            res.add(continusRange);
        }
        if (notTen == 0 && notTenRange != null) {
            res.add(notTenRange);
        }
        if (tens == 0 && TenRange != null) {
            res.add(TenRange);
        }

        if (hundreds == 0 && hundredRange != null) {
            res.add(hundredRange);
        }
        List<AmountMaxMinType> collect1 = res.stream().distinct().collect(Collectors.toList());
//        System.out.println(JSONObject.toJSONString(res));

        HashMap<String, List<AmountMaxMinType>> merge = merge(collect1);
        supportAmount.setAmountType(merge);
//        merge(res);
        return supportAmount;
    }

    private AmountMaxMinType update(AmountMaxMinType continusRange, AmountMaxMinType other) {
        if (continusRange == null || other == null)
            return null;
        if (continusRange.getMax() >= other.getMax() && continusRange.getMin() <= other.getMin()) {
            other.setMax(other.getMin());
            return null;
        }
        if (continusRange.getMax() == other.getMax() && continusRange.getMin() < other.getMin()) {
            other.setMax(other.getMin());
            return null;
        }
        if (continusRange.getMin() == other.getMin() && continusRange.getMax() > other.getMax()) {
            other.setMax(other.getMin());
            return null;
        }

        //          |-------------------------|
//                                  |---------|
        if (continusRange.getMax() < other.getMax() && continusRange.getMax() > other.getMin() && continusRange.getMin() < other.getMin()) {
            other.setMin(continusRange.getMax());
        }

//          |-------------------------|
//     |---------|

        if (continusRange.getMin() < other.getMax() && continusRange.getMax() > other.getMax() && other.getMin() < continusRange.getMin()) {
            other.setMax(continusRange.getMin());
        }

        //          |-------------------------|
//     |---------------------------other------------------------|
        if (other.getMin() < continusRange.getMin() && other.getMax() > continusRange.getMax()) {
            AmountMaxMinType amountMaxMinType = new AmountMaxMinType(other.getMin(), continusRange.getMin(), other.getType());
            other.setMin(continusRange.getMax());
            return amountMaxMinType;
        }
        return null;
    }

    private HashMap<String, List<AmountMaxMinType>> merge(List<AmountMaxMinType> res) {
        HashMap<String, List<AmountMaxMinType>> tmp = new HashMap<>();

        for (AmountMaxMinType a : res) {
            List<AmountMaxMinType> existsLists = tmp.get(a.getType());
            if (existsLists == null) {
                ArrayList<AmountMaxMinType> amountMaxMinTypes = new ArrayList<>();
                if (!a.getMax().equals(a.getMin())) {
                    amountMaxMinTypes.add(a);
                    tmp.put(a.getType(), amountMaxMinTypes);
                }
            } else {
                ArrayList<AmountMaxMinType> amountMaxMinTypes = new ArrayList<>();
                amountMaxMinTypes.addAll(existsLists);
                for (AmountMaxMinType i : existsLists) {
                    if (i != null && a.getMax().equals(a.getMin())) {
                        continue;
                    }
                    if (i != null && i.getMin() > a.getMax() || i.getMax() < a.getMin()) {
                        amountMaxMinTypes.add(a);
                        tmp.put(i.getType(), amountMaxMinTypes);
                    }
                    if (i != null && a.getMax() >= i.getMin() && a.getMax() <= i.getMax() && i.getMin() > a.getMin()) {
                        i.setMin(a.getMin());
                    }
                    if (i != null && i.getMin() <= a.getMin() && a.getMin() <= i.getMax() && i.getMax() < a.getMax()) {
                        i.setMax(a.getMax());
                    }

                }
            }

        }
        return tmp;
    }

    private SupportAmount sigal(MchPayPassage mchPayPassage) {
        Long max = 0l;
        Long min = 9999999l;
        List<Long> collect = new ArrayList<>();
        HashMap<String, List<AmountMaxMinType>> integerHashMapHashMap = new HashMap<>();
        SupportAmount supportAmount = new SupportAmount();
        PayPassage passage = rpcCommonService.rpcPayPassageService.findById(mchPayPassage.getPayPassageId());
        if (null == passage || passage.getRiskStatus() == 0)
            return supportAmount;
        String fixAmount = passage.getFixAmount();
        if (StringUtils.isNotBlank(fixAmount)) {
            collect.addAll(Arrays.stream(fixAmount.split(",")).map(e -> Long.parseLong(e)).collect(Collectors.toList()));
            supportAmount.setFixAmount(collect);
        }
        if (max < passage.getMaxEveryAmount()) {
            max = passage.getMaxEveryAmount();
            supportAmount.setMax(max / 100);
        }
        if (min > passage.getMinEveryAmount()) {
            min = passage.getMinEveryAmount();
            supportAmount.setMin(min / 100);
        }
        AmountMaxMinType type = new AmountMaxMinType();
        type.setMax(max / 100);
        type.setMin(min / 100);
        type.setType(passage.getAmountType().toString());
        ArrayList<AmountMaxMinType> amountMaxMinTypes = new ArrayList<>();
        amountMaxMinTypes.add(type);
        if (passage.getAmountType() != 3)
            integerHashMapHashMap.put(passage.getAmountType().toString(), amountMaxMinTypes);
        supportAmount.setAmountType(integerHashMapHashMap);
        return supportAmount;
    }

    class AmountMaxMinType {
        String type;
        Long min;
        Long max;

        AmountMaxMinType() {

        }

        AmountMaxMinType(Long min, Long max, String type) {
            this.max = max;
            this.min = min;
            this.type = type;
        }

        @Override
        public int hashCode() {
            return max.hashCode() + min.hashCode() + type.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            AmountMaxMinType obj1 = (AmountMaxMinType) obj;
            return this.getMax() == obj1.getMax() &&
                    this.getMin() == obj1.getMin() &&
                    this.getType() == obj1.getType();
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getMin() {
            return min;
        }

        public void setMin(Long min) {
            this.min = min;
        }

        public Long getMax() {
            return max;
        }

        public void setMax(Long max) {
            this.max = max;
        }
    }
}
