package org.xxpay.asst.secruity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.xxpay.asst.user.service.UserService;
import org.xxpay.core.entity.AssistantInfo;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AssistantInfo AssistantInfo = userService.findByLoginName(username);
        if (AssistantInfo == null) {
            //throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
            return null;
        } else {
            return JwtUserFactory.create(username, AssistantInfo);
        }
    }
}
