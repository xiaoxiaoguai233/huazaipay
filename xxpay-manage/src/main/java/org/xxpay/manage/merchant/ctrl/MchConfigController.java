package org.xxpay.manage.merchant.ctrl;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.enumm.ConfigTypeEnum;
import org.xxpay.core.entity.MchAccount;
import org.xxpay.core.entity.MchConfig;
import org.xxpay.manage.common.ctrl.BaseController;
import org.xxpay.manage.common.service.RpcCommonService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(Constant.MGR_CONTROLLER_ROOT_PATH + "/mch_config")
public class MchConfigController extends BaseController {

    @Autowired
    private RpcCommonService rpcCommonService;

    @RequestMapping("/add")
    public ResponseEntity<?> add(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchConfig mchConfig = getObject(param, MchConfig.class);
        if(StringUtils.isNotBlank(String.valueOf(mchConfig.getMchId())) && 1== mchConfig.getConfigType()){
            MchAccount mchAccount=rpcCommonService.rpcMchAccountService.findByMchId(mchConfig.getMchId());
            if(StringUtils.isBlank(String.valueOf(mchAccount.getMchId()))) return ResponseEntity.ok(BizResponse.build(RetEnum.RET_SERVICE_MCH_NOT_EXIST));
        }
        if (null != rpcCommonService.rpcPayMchConfigService.getGlobal(request.getParameter("configName")) || null != rpcCommonService.rpcPayMchConfigService.get(mchConfig.getMchId(), request.getParameter("configName"))) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MGR_MCH_CONFIG_EXISTS));
        }
        rpcCommonService.rpcPayMchConfigService.add(mchConfig);
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    @RequestMapping("/update")
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        MchConfig mchConfig = getObject(param, MchConfig.class);
        MchConfig globalObj = null;
        globalObj  = rpcCommonService.rpcPayMchConfigService.getGlobal(mchConfig.getConfigName());
        if (null != globalObj ) {
            rpcCommonService.rpcPayMchConfigService.update(mchConfig);
        }
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }


    @RequestMapping("/list")
    public ResponseEntity<?> list(HttpServletRequest request) {
        String mchId = request.getParameter("mchId");
        String configType = request.getParameter("configType");
        Long paramMchId =0L;
        Byte ctp=null;
        if(StringUtils.isBlank(mchId)){
            paramMchId=null;
        }else{
            paramMchId=Long.parseLong(mchId);
        }
        if("-99".equals(configType)|| StringUtils.isBlank(configType)){
            ctp=null;
        }else{
            ctp = (byte) Integer.parseInt(configType);
        }
        List<MchConfig> list = rpcCommonService.rpcPayMchConfigService.list(paramMchId,ctp);
        for(MchConfig mc:list){
            mc.setConfigTypeDicMap(ConfigTypeEnum.descMap());
        }
        return ResponseEntity.ok(XxPayResponse.buildSuccess(list));
    }

    @RequestMapping("/getById")
    public ResponseEntity<?> get(HttpServletRequest request) {
        String id = request.getParameter("id");
        MchConfig jsonObject = new MchConfig();
        if (StringUtils.isNotBlank(id)) {
            jsonObject = rpcCommonService.rpcPayMchConfigService.getById(Long.parseLong(id));
        } else {
           return  ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_PARAM_ERROR));
        }
        jsonObject.setConfigTypeDicMap(ConfigTypeEnum.descMap());
        return ResponseEntity.ok(XxPayResponse.buildSuccess(jsonObject));
    }

    @RequestMapping("/getByMchId")
    public ResponseEntity<?> getByMchId(HttpServletRequest request) {
        String mchId = request.getParameter("mchId");
        MchConfig mchConfig = new MchConfig();
        if("null".equals(mchId)){
            mchConfig.setMchId(null);
        }else{
            mchConfig.setMchId(Long.parseLong(mchId));
        }
        List<MchConfig>  arraList = new ArrayList<MchConfig>();
        arraList = rpcCommonService.rpcPayMchConfigService.getByMchId(mchConfig.getMchId());
//        jsonObject.setConfigTypeDicMap(ConfigTypeEnum.descMap());
        return ResponseEntity.ok(XxPayResponse.buildSuccess(arraList));
    }

}
