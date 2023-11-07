package org.xxpay.asst.user.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.asst.common.ctrl.BaseController;
import org.xxpay.asst.common.service.RpcCommonService;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.GoogleAuthenticator;
import org.xxpay.core.entity.AssistantInfo;
import org.xxpay.core.entity.MchInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 代理商安全中心
 */
@Controller
@RequestMapping(Constant.ASST_CONTROLLER_ROOT_PATH + "/security")
@PreAuthorize("hasRole('"+ MchConstant.ASST_ROLE_NORMAL+"')")
public class SecurityCenterController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    /**
     * 得到用户谷歌验证二维码
     * @return
     */
    @RequestMapping("/google_qrcode")
    @ResponseBody
    public ResponseEntity<?> getGoogleAuthQrCode() {
        AssistantInfo AssistantInfo = rpcCommonService.rpcAssistantInfoService.findByAssistantId(getUser().getId());
        Long mobile = AssistantInfo.getMobile();
        String googleAuthSecretKey = AssistantInfo.getGoogleAuthSecretKey();
        if(StringUtils.isBlank(googleAuthSecretKey)){
            googleAuthSecretKey = GoogleAuthenticator.generateSecretKey();
            AssistantInfo updateAssistantInfo = new AssistantInfo();
            updateAssistantInfo.setAssistantId(AssistantInfo.getAssistantId());
            updateAssistantInfo.setGoogleAuthSecretKey(googleAuthSecretKey);
            int count = rpcCommonService.rpcAssistantInfoService.update(updateAssistantInfo);
            if(count != 1) return ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        }
        String qrcode = GoogleAuthenticator.getQRBarcode("Assistant("+mobile+")", googleAuthSecretKey);
        String qrcodeUrl = mainConfig.getPayUrl() + "/qrcode_img_get?url=" + qrcode + "&widht=200&height=200";
        return ResponseEntity.ok(XxPayResponse.buildSuccess(qrcodeUrl));
    }

    /**
     * 绑定谷歌验证
     * @return
     */
    @RequestMapping("/google_bind")
    @ResponseBody
    public ResponseEntity<?> bindGoogleAuth(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        // 获取传的验证码
        Long code = getLongRequired(param, "code");
        if(!checkGoogleCode(getUser().getId(), code)) return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH));
        // 修改数据中绑定状态
        AssistantInfo updateAssistantInfo = new AssistantInfo();
        updateAssistantInfo.setAssistantId(getUser().getId());
        updateAssistantInfo.setGoogleAuthStatus(MchConstant.PUB_YES);
        int count = rpcCommonService.rpcAssistantInfoService.update(updateAssistantInfo);
        if(count != 1) return ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_MCH_GOOGLEAUTH_SECRETKEY_BIND_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    /**
     * 设置登录验证方式
     * @return
     */
    @RequestMapping("/login_set")
    @ResponseBody
    @MethodLog( remark = "设置登录验证方式" )
    public ResponseEntity<?> setLogin(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Byte loginSecurityType = getByteRequired(param, "loginSecurityType");
        Long code = getLongRequired(param, "code");
        if(!checkGoogleCode(getUser().getId(), code)) return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH));
        if(loginSecurityType == 1) {    // 登录密码+谷歌验证组合
            MchInfo queryMchInfo = rpcCommonService.rpcMchInfoService.findByMchId(getUser().getId());
            if(queryMchInfo.getGoogleAuthStatus() != MchConstant.PUB_YES) {
                // 没有绑定谷歌
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLEAUTH_NOT_BIND));
            }
        }
        AssistantInfo updateAssistantInfo = new AssistantInfo();
        updateAssistantInfo.setAssistantId(getUser().getId());
        updateAssistantInfo.setLoginSecurityType(loginSecurityType);
        int count = rpcCommonService.rpcAssistantInfoService.update(updateAssistantInfo);
        if(count != 1) return ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    /**
     * 设置支付验证方式
     * @return
     */
    @RequestMapping("/pay_set")
    @ResponseBody
    @MethodLog( remark = "设置支付验证方式" )
    public ResponseEntity<?> setPay(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        Byte paySecurityType = getByteRequired(param, "paySecurityType");
        Long code = getLongRequired(param, "code");
        if(!checkGoogleCode(getUser().getId(), code)) return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLECODE_NOT_MATCH));
        if(paySecurityType == 2 || paySecurityType == 3) {    // 判断需要绑定谷歌验证
            AssistantInfo queryAssistantInfo = rpcCommonService.rpcAssistantInfoService.findByAssistantId(getUser().getId());
            if(queryAssistantInfo.getGoogleAuthStatus() != MchConstant.PUB_YES) {
                // 没有绑定谷歌
                return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_GOOGLEAUTH_NOT_BIND));
            }
        }
        AssistantInfo updateAssistantInfo = new AssistantInfo();
        updateAssistantInfo.setAssistantId(getUser().getId());
        updateAssistantInfo.setPaySecurityType(paySecurityType);
        int count = rpcCommonService.rpcAssistantInfoService.update(updateAssistantInfo);
        if(count != 1) return ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    /**
     * 验证谷歌验证码
     * @param AssistantId
     * @param code
     * @return
     */
    boolean checkGoogleCode(Long AssistantId, Long code) {
        AssistantInfo AssistantInfo = rpcCommonService.rpcAssistantInfoService.findByAssistantId(AssistantId);
        String googleAuthSecretKey = AssistantInfo.getGoogleAuthSecretKey();
        return checkGoogleCode(googleAuthSecretKey, code);
    }

}