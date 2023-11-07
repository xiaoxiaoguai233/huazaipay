package org.xxpay.manage.assistant.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.common.util.StrUtil;
import org.xxpay.core.entity.AssistantAccount;
import org.xxpay.core.entity.AssistantInfo;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/assistant_info")
public class AssistantInfoController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    /**
     * 查询代理商信息
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<?> get(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long AssistantId = getLongRequired(param, "AssistantId");
        AssistantInfo AssistantInfo = rpcCommonService.rpcAssistantInfoService.findByAssistantId(AssistantId);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(AssistantInfo));
    }

    /**
     * 查询一级、二级代理商ID
     * @return
     */
    @RequestMapping("/getParentAssistantId")
    @ResponseBody
    public ResponseEntity<?> getParentAssistantId(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Byte AssistantLevel = getByte(param, "AssistantLevel");
        AssistantInfo AssistantInfo = new AssistantInfo();
        if (AssistantLevel != null) {
            AssistantInfo.setAssistantLevel(AssistantLevel);
        }
        AssistantInfo.setStatus(MchConstant.PUB_YES);
        List<AssistantInfo> list = rpcCommonService.rpcAssistantInfoService.selectAll(AssistantInfo);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(list));
    }

    /**
     * 新增代理商信息
     * @return
     */
    @RequestMapping("/add")
    @ResponseBody
    @MethodLog( remark = "新增代理商" )
    public ResponseEntity<?> add(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        AssistantInfo AssistantInfo = getObject(param, AssistantInfo.class);
        // 设置默认登录密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = MchConstant.MCH_DEFAULT_PASSWORD;
        AssistantInfo.setPassword(encoder.encode(rawPassword));
        AssistantInfo.setLastPasswordResetTime(new Date());
        // 设置默认支付密码
        String payPassword = MchConstant.MCH_DEFAULT_PAY_PASSWORD;
        AssistantInfo.setPayPassword(MD5Util.string2MD5(payPassword));
        // 确认用户名不能重复
        if(rpcCommonService.rpcAssistantInfoService.findByUserName(AssistantInfo.getUserName()) != null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_ASST_USERNAME_USED));
        }
        // 确认手机不能重复
        if(rpcCommonService.rpcAssistantInfoService.findByMobile(AssistantInfo.getMobile()) != null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_MOBILE_USED));
        }
        // 确认邮箱不能重复
        if(rpcCommonService.rpcAssistantInfoService.findByEmail(AssistantInfo.getEmail()) != null) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_EMAIL_USED));
        }

        // 验证一级代理ID
//        if (AssistantInfo.getParentAssistantId() == null) AssistantInfo.setParentAssistantId(0L);
//        if(AssistantInfo.getParentAssistantId() != 0) {
//            if (rpcCommonService.rpcAssistantInfoService.findByAssistantId(AssistantInfo.getParentAssistantId()) == null) {
//                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PARENTAssistantID_NOT_EXIST));
//            }
//            if(rpcCommonService.rpcAssistantInfoService.findByAssistantId(AssistantInfo.getParentAssistantId()).getAssistantLevel() != 1){
//                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PARENTAssistantID_NOT_EXIST));
//            }
//            AssistantInfo.setAssistantLevel((byte) 2);
//        }else{
//            AssistantInfo.setAssistantLevel((byte) 1);
//        }

        int count = rpcCommonService.rpcAssistantInfoService.add(AssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(BizResponse.buildSuccess());
    }

    /**
     * 修改代理商信息
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    @MethodLog( remark = "修改代理商" )
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long AssistantId = getLongRequired(param, "AssistantId");
        AssistantInfo AssistantInfo = getObject(param, AssistantInfo.class);
        AssistantInfo dbAssistantInfo = rpcCommonService.rpcAssistantInfoService.findByAssistantId(AssistantId);
        if(dbAssistantInfo == null) {
            return ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_RECORD_NOT_EXIST));
        }

        if(AssistantInfo.getMobile() != null && AssistantInfo.getMobile().longValue() != dbAssistantInfo.getMobile().longValue()) {
            // 确认手机不能重复
            if(rpcCommonService.rpcAssistantInfoService.findByMobile(AssistantInfo.getMobile()) != null) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_MOBILE_USED));
            }
        }

        if(AssistantInfo.getEmail() != null && !AssistantInfo.getEmail().equalsIgnoreCase(dbAssistantInfo.getEmail())) {
            // 确认邮箱不能重复
            if(rpcCommonService.rpcAssistantInfoService.findByEmail(AssistantInfo.getEmail()) != null) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_EMAIL_USED));
            }
        }

        //判断上级代理商
//        if (AssistantInfo.getParentAssistantId() != null && AssistantInfo.getParentAssistantId() != 0){
//            AssistantInfo parentAssistantInfo = rpcCommonService.rpcAssistantInfoService.findByAssistantId(AssistantInfo.getParentAssistantId());
//            if (parentAssistantInfo == null) return ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_SERVICE_Assistant_NOT_EXIST));
//        }

        // 商户信息只允许修改手机号,邮箱,费率,结算方式,状态,其他不允许修改
        AssistantInfo updateAssistantInfo = new AssistantInfo();
        updateAssistantInfo.setAssistantId(AssistantId);
        updateAssistantInfo.setParentAssistantId(AssistantInfo.getParentAssistantId());
        updateAssistantInfo.setAssistantLevel(AssistantInfo.getAssistantLevel());
        updateAssistantInfo.setAssistantName(AssistantInfo.getAssistantName());
        updateAssistantInfo.setMobile(AssistantInfo.getMobile());
        updateAssistantInfo.setEmail(AssistantInfo.getEmail());
        updateAssistantInfo.setSettType(AssistantInfo.getSettType());
        updateAssistantInfo.setStatus(AssistantInfo.getStatus());
        updateAssistantInfo.setRealName(AssistantInfo.getRealName());
        updateAssistantInfo.setIdCard(AssistantInfo.getIdCard());
        updateAssistantInfo.setTel(AssistantInfo.getTel());
        updateAssistantInfo.setQq(AssistantInfo.getQq());
        updateAssistantInfo.setAddress(AssistantInfo.getAddress());
        updateAssistantInfo.setOffRechargeRate(AssistantInfo.getOffRechargeRate());
        updateAssistantInfo.setBankName(AssistantInfo.getBankName());
        updateAssistantInfo.setBankNetName(AssistantInfo.getBankNetName());
        updateAssistantInfo.setAccountName(AssistantInfo.getAccountName());
        updateAssistantInfo.setAccountNo(AssistantInfo.getAccountNo());
        updateAssistantInfo.setProvince(AssistantInfo.getProvince());
        updateAssistantInfo.setCity(AssistantInfo.getCity());
        updateAssistantInfo.setLoginSecurityType(AssistantInfo.getLoginSecurityType());
        updateAssistantInfo.setPaySecurityType(AssistantInfo.getPaySecurityType());
        // 如果登录密码不为空,则修改登录密码
        String rawPassword = AssistantInfo.getPassword();
        if(StringUtils.isNotBlank(rawPassword)) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            // 判断新登录密码格式
            if(!StrUtil.checkPassword(rawPassword)) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PASSWORD_FORMAT_FAIL));
            }
            updateAssistantInfo.setPassword(encoder.encode(rawPassword));
            updateAssistantInfo.setLastPasswordResetTime(new Date());
        }
        // 如果支付密码不为空,则修改支付密码
        String payPassword = AssistantInfo.getPayPassword();
        if(StringUtils.isNotBlank(payPassword)) {
            // 判断新支付密码格式
            if(!StrUtil.checkPassword(payPassword)) {
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PASSWORD_FORMAT_FAIL));
            }
            updateAssistantInfo.setPayPassword(MD5Util.string2MD5(payPassword));
        }
        int count = rpcCommonService.rpcAssistantInfoService.update(updateAssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(BizResponse.buildSuccess());
    }

    /**
     * 修改结算设置
     * @return
     */
    @RequestMapping("/sett_update")
    @ResponseBody
    @MethodLog( remark = "修改代理商结算信息" )
    public ResponseEntity<?> updateSett(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        handleParamAmount(param, "drawMaxDayAmount", "maxDrawAmount", "minDrawAmount", "drawFeeLimit");
        Long AssistantId = getLongRequired(param, "AssistantId");
        AssistantInfo AssistantInfo = getObject(param, AssistantInfo.class);
        // 修改结算设置信息
        AssistantInfo updateAssistantInfo = new AssistantInfo();
        updateAssistantInfo.setAssistantId(AssistantId);

        updateAssistantInfo.setSettConfigMode(AssistantInfo.getSettConfigMode());
        if(AssistantInfo.getSettConfigMode() == 2) {    // 自定义
            updateAssistantInfo.setDrawFlag(AssistantInfo.getDrawFlag());                   // 提现开关
            updateAssistantInfo.setAllowDrawWeekDay(AssistantInfo.getAllowDrawWeekDay());   // 每周几允许提现
            updateAssistantInfo.setDrawDayStartTime(AssistantInfo.getDrawDayStartTime());   // 每日提现开始时间
            updateAssistantInfo.setDrawDayEndTime(AssistantInfo.getDrawDayEndTime());       // 每日提现结束时间
            updateAssistantInfo.setDayDrawTimes(AssistantInfo.getDayDrawTimes());           // 每日最多提现次数
            checkRequired(param, "drawMaxDayAmount", "maxDrawAmount", "minDrawAmount");
            updateAssistantInfo.setDrawMaxDayAmount(AssistantInfo.getDrawMaxDayAmount());   // 每日提现最多金额
            updateAssistantInfo.setMaxDrawAmount(AssistantInfo.getMaxDrawAmount());         // 每笔提现最大金额
            updateAssistantInfo.setMinDrawAmount(AssistantInfo.getMinDrawAmount());         // 每笔提现最小金额
            updateAssistantInfo.setFeeType(AssistantInfo.getFeeType());                     // 结算手续费类型
            if(AssistantInfo.getFeeType() == 1) {
                // 百分比收费
                updateAssistantInfo.setFeeRate(AssistantInfo.getFeeRate());
            }else if(AssistantInfo.getFeeType() == 2) {
                // 固定收费
                Long feeLevelL = getRequiredAmountL(param, "feeLevel");
                updateAssistantInfo.setFeeLevel(String.valueOf(feeLevelL));
            }
            updateAssistantInfo.setDrawFeeLimit(AssistantInfo.getDrawFeeLimit());           // 单笔手续费上限
            updateAssistantInfo.setSettType(AssistantInfo.getSettType());                   // 结算类型
            updateAssistantInfo.setSettMode(AssistantInfo.getSettMode());                   // 结算方式
        }
        int count = rpcCommonService.rpcAssistantInfoService.update(updateAssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(BizResponse.buildSuccess());
    }    

    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        AssistantInfo AssistantInfo = getObject(param, AssistantInfo.class);
        int count = rpcCommonService.rpcAssistantInfoService.count(AssistantInfo);
        if(count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<AssistantInfo> AssistantInfoList = rpcCommonService.rpcAssistantInfoService.select((getPageIndex(param) - 1) * getPageSize(param), getPageSize(param), AssistantInfo);
        List<JSONObject> objects = new LinkedList<>();
        for(AssistantInfo info : AssistantInfoList) {
            JSONObject object = (JSONObject) JSON.toJSON(info);
            object.put("loginAssistantUrl", buildLoginAssistantUrl(info));  // 生成登录地址
            AssistantAccount account = rpcCommonService.rpcAssistantAccountService.findByAssistantId(info.getAssistantId());
            object.put("AssistantBalance", account.getBalance());  // 账户余额
            objects.add(object);
        }
        Map<String ,Object> ps = new HashMap<String, Object>();
        AssistantAccount accountRecord = new AssistantAccount();
        accountRecord.setAssistantId(AssistantInfo.getAssistantId());
        ps.put("allAssistantBalance",  rpcCommonService.rpcAssistantAccountService.sumAssistantBalance(accountRecord));
        return ResponseEntity.ok(XxPayPageRes.buildSuccess(objects, ps, count));
    }

    /**
     * 重置商户密码
     * @return
     */
    @RequestMapping("/pwd_reset")
    @ResponseBody
    @MethodLog( remark = "重置代理商密码" )
    public ResponseEntity<?> resetPwd(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long AssistantId = getLongRequired(param, "AssistantId");
        String rawPassword = getStringRequired(param, "password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        // 判断新密码格式
        if(!StrUtil.checkPassword(rawPassword)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PASSWORD_FORMAT_FAIL));
        }
        AssistantInfo AssistantInfo = new AssistantInfo();
        AssistantInfo.setAssistantId(AssistantId);
        AssistantInfo.setPassword(encoder.encode(rawPassword));
        AssistantInfo.setLastPasswordResetTime(new Date());
        int count = rpcCommonService.rpcAssistantInfoService.update(AssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    /**
     * 解绑谷歌验证
     * @return
     */
    @RequestMapping("/google_untie")
    @ResponseBody
    @MethodLog( remark = "解绑代理商谷歌验证" )
    public ResponseEntity<?> untieGoogleAuth(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Long AssistantId = getLongRequired(param, "AssistantId");
        AssistantInfo AssistantInfo = new AssistantInfo();
        AssistantInfo.setAssistantId(AssistantId);
        AssistantInfo.setGoogleAuthStatus(MchConstant.PUB_NO);
        AssistantInfo.setGoogleAuthSecretKey("");
        int count = rpcCommonService.rpcAssistantInfoService.update(AssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    private String buildLoginAssistantUrl(AssistantInfo AssistantInfo) {
        // 将商户ID+商户密码+密钥 做32位MD5加密转大写,作为token传递给商户系统
        String password = AssistantInfo.getPassword();
        String secret = "Abc%$G&!!!128G";
        String rawToken = AssistantInfo.getAssistantId() + password + secret;
        String token = MD5Util.string2MD5(rawToken).toUpperCase();
        String loginAssistantUrl = mainConfig.getLoginAssistantUrl();
        return String.format(loginAssistantUrl, AssistantInfo.getAssistantId(), token);
    }


}