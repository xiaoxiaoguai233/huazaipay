package org.xxpay.asst.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.xxpay.asst.common.service.RpcCommonService;
import org.xxpay.asst.secruity.JwtTokenUtil;
import org.xxpay.asst.secruity.JwtUser;
import org.xxpay.core.common.Exception.ServiceException;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.common.constant.RetEnum;
import org.xxpay.core.common.util.MyLog;
import org.xxpay.core.entity.AssistantInfo;

/**
 * Created by dingzhiwei on 17/11/28.
 */
@Component
public class UserService {

    @Autowired
    private RpcCommonService rpcCommonService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private static final MyLog _log = MyLog.getLog(UserService.class);

    public AssistantInfo findByUserName(String userName) {
        return rpcCommonService.rpcAssistantInfoService.findByUserName(userName);
    }

    public AssistantInfo findByAssistantId(Long AssistantId) {
        return rpcCommonService.rpcAssistantInfoService.findByAssistantId(AssistantId);
    }

    public AssistantInfo findByLoginName(String loginName) {
        return rpcCommonService.rpcAssistantInfoService.findByLoginName(loginName);
    }

    public AssistantInfo findByMobile(Long mobile) {
        return rpcCommonService.rpcAssistantInfoService.findByMobile(mobile);
    }

    public AssistantInfo findByEmail(String email) {
        return rpcCommonService.rpcAssistantInfoService.findByEmail(email);
    }

    public String login(String username, String password) throws ServiceException {
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
        // Perform the security
        try{
            Authentication authentication = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (Exception e) {
            _log.error(e, "鉴权失败");
            throw new ServiceException(RetEnum.RET_MCH_AUTH_FAIL);
        }

        // Reload password post-security so we can generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // 查看商户状态
        JwtUser jwtUser = (JwtUser) userDetails;
        Byte status = jwtUser.getStatus();
        if(status == MchConstant.STATUS_AUDIT_ING) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_AUDIT_ING);
        }else if(status == MchConstant.STATUS_STOP) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_STOP);
        }

        String token = jwtTokenUtil.generateToken(userDetails);
        return token;
    }

    public String refreshToken(String oldToken) {
        String token = oldToken;
        String username = jwtTokenUtil.getUsernameFromToken(token);
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
        // 查看商户状态
        Byte status = user.getStatus();
        if(status == MchConstant.STATUS_AUDIT_ING) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_AUDIT_ING);
        }else if(status == MchConstant.STATUS_STOP) {
            throw new ServiceException(RetEnum.RET_MCH_STATUS_STOP);
        }

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())){
            return jwtTokenUtil.refreshToken(token);
        }
        return null;
    }

}
