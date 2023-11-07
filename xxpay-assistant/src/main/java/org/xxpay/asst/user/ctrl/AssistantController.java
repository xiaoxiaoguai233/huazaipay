package org.xxpay.asst.user.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xxpay.asst.common.ctrl.BaseController;
import org.xxpay.asst.common.service.RpcCommonService;
import org.xxpay.asst.user.service.UserService;
import org.xxpay.core.common.annotation.MethodLog;
import org.xxpay.core.common.constant.Constant;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.domain.BizResponse;
import org.xxpay.core.common.domain.MenuTreeBuilder;
import org.xxpay.core.common.domain.XxPayPageRes;
import org.xxpay.core.common.domain.XxPayResponse;
import org.xxpay.core.common.util.MD5Util;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.common.util.StrUtil;
import org.xxpay.core.entity.AssistantAccount;
import org.xxpay.core.entity.AssistantInfo;
import org.xxpay.core.entity.SysResource;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping(Constant.ASST_CONTROLLER_ROOT_PATH + "/assistant")
@PreAuthorize("hasRole('"+ MchConstant.ASST_ROLE_NORMAL+"')")
public class AssistantController extends BaseController {

    private final static MyLog _log = MyLog.getLog(AssistantController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RpcCommonService rpcCommonService;

    /**
     * 查询代理商信息
     * @return
     */
    @RequestMapping("/get")
    @ResponseBody
    public ResponseEntity<?> get() {
        AssistantInfo AssistantInfo = userService.findByAssistantId(getUser().getId());
        // 一些结算配置信息,有可能是集成了系统默认,需要重构下对象
        AssistantInfo = rpcCommonService.rpcAssistantInfoService.reBuildAssistantInfoSettConfig(AssistantInfo);
        return ResponseEntity.ok(XxPayResponse.buildSuccess(AssistantInfo));
    }


    /**
     * 查询下级代理商信息
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public ResponseEntity<?> list(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        AssistantInfo AssistantInfo = getObject(param, AssistantInfo.class);
        AssistantInfo.setParentAssistantId(getUser().getId());
        int count = rpcCommonService.rpcAssistantInfoService.count(AssistantInfo);
        if(count == 0) return ResponseEntity.ok(XxPayPageRes.buildSuccess());
        List<AssistantInfo> AssistantInfoList = rpcCommonService.rpcAssistantInfoService.select((getPageIndex(param) - 1) * getPageSize(param), getPageSize(param), AssistantInfo);
        List<JSONObject> objects = new LinkedList<>();
        for(AssistantInfo info : AssistantInfoList) {
            JSONObject object = (JSONObject) JSON.toJSON(info);
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
     * 查询菜单
     * @return
     */
    @RequestMapping("/menu_get")
    @ResponseBody
    public ResponseEntity<?> getMenu() {
        Byte AssistantLevel = rpcCommonService.rpcAssistantInfoService.findByAssistantId(getUser().getId()).getAssistantLevel();
        List<SysResource> sysResourceList = rpcCommonService.rpcSysService.selectAllResource(MchConstant.SYSTEM_ASST);
        List<MenuTreeBuilder.Node> nodeList = new LinkedList<>();
        for(SysResource sysResource : sysResourceList) {
            // 判断是否显示该菜单,一级代理和二级代理不同
            // 用资源表中的property区分,该值为空都可见.否则对应代理等级,如1 表示一级代理可见, 1,2 表示一级、二级代理都可见
            boolean isShow = true;
            String property = sysResource.getProperty();
            if(StringUtils.isNotBlank(property)) {
                isShow = false;
                String[] propertys = property.split(",");
                for(String str : propertys) {
                    if(AssistantLevel != null && str.equalsIgnoreCase(AssistantLevel.toString())) {
                        isShow = true;
                        break;
                    }
                }
            }
            if(!isShow) continue;
            MenuTreeBuilder.Node node = new MenuTreeBuilder.Node();
            node.setResourceId(sysResource.getResourceId());
            node.setName(sysResource.getName());
            node.setTitle(sysResource.getTitle());
            if(StringUtils.isNotBlank(sysResource.getJump())) node.setJump(sysResource.getJump());
            if(StringUtils.isNotBlank(sysResource.getIcon())) node.setIcon(sysResource.getIcon());
            node.setParentId(sysResource.getParentId());
            nodeList.add(node);
        }
        return ResponseEntity.ok(XxPayResponse.buildSuccess(JSONArray.parseArray(MenuTreeBuilder.buildTree(nodeList))));
    }

    /**
     * 修改代理商信息
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    @MethodLog( remark = "修改代理商信息" )
    public ResponseEntity<?> update(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        AssistantInfo AssistantInfo = new AssistantInfo();
        AssistantInfo.setAssistantId(getUser().getId());
        AssistantInfo.setRemark(param.getString("remark"));
        int count = rpcCommonService.rpcAssistantInfoService.update(AssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    /**
     * 修改登录密码
     * @return
     */
    @RequestMapping("/pwd_update")
    @ResponseBody
    @MethodLog( remark = "修改密码" )
    public ResponseEntity<?> updatePassword(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        // 旧密码
        String oldRawPassword = getStringRequired(param, "oldPassword");
        // 新密码
        String rawPassword = getStringRequired(param, "password");
        // 验证旧密码是否正确
        AssistantInfo AssistantInfo = userService.findByAssistantId(getUser().getId());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(oldRawPassword, AssistantInfo.getPassword())) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_OLDPASSWORD_NOT_MATCH));
        }
        // 判断新密码格式
        if(!StrUtil.checkPassword(rawPassword)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PASSWORD_FORMAT_FAIL));
        }
        AssistantInfo = new AssistantInfo();
        AssistantInfo.setAssistantId(getUser().getId());
        AssistantInfo.setPassword(encoder.encode(rawPassword));
        AssistantInfo.setLastPasswordResetTime(new Date());
        int count = rpcCommonService.rpcAssistantInfoService.update(AssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }

    /**
     * 修改支付密码
     * @return
     */
    @RequestMapping("/paypwd_update")
    @ResponseBody
    @MethodLog( remark = "修改支付密码" )
    public ResponseEntity<?> updatePayPassword(HttpServletRequest request) {
        JSONObject param = getJsonParam(request);
        // 旧支付密码
        String oldPayPassword = getStringRequired(param, "oldPayPassword");
        // 新支付密码
        String payPassword = getStringRequired(param, "payPassword");
        // 验证旧支付密码
        AssistantInfo AssistantInfo = userService.findByAssistantId(getUser().getId());
        if(!MD5Util.string2MD5(oldPayPassword).equals(AssistantInfo.getPayPassword())) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_OLDPASSWORD_NOT_MATCH));
        }
        // 判断新支付密码格式
        if(!StrUtil.checkPassword(payPassword)) {
            return ResponseEntity.ok(BizResponse.build(RetEnum.RET_MCH_PASSWORD_FORMAT_FAIL));
        }
        AssistantInfo = new AssistantInfo();
        AssistantInfo.setAssistantId(getUser().getId());
        AssistantInfo.setPayPassword(MD5Util.string2MD5(payPassword));
        int count = rpcCommonService.rpcAssistantInfoService.update(AssistantInfo);
        if(count != 1) ResponseEntity.ok(XxPayResponse.build(RetEnum.RET_COMM_OPERATION_FAIL));
        return ResponseEntity.ok(XxPayResponse.buildSuccess());
    }


}