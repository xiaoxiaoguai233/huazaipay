package org.xxpay.asst.secruity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.xxpay.core.common.constant.MchConstant;
import org.xxpay.core.entity.AssistantInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(String userName, AssistantInfo AssistantInfo) {
        return new JwtUser(
                AssistantInfo.getAssistantId(),
                AssistantInfo.getAssistantName(),
                AssistantInfo.getStatus(),
                userName,
                AssistantInfo.getPassword(),
                AssistantInfo.getEmail(),
                mapToGrantedAuthorities(MchConstant.ASST_ROLE_NORMAL),
                AssistantInfo.getLastPasswordResetTime()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(String role) {
        String[] roles = role.split(":");
        List<String> authorities = Arrays.asList(roles);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}