package com.liser.oauth2.authentication.provider;

import com.liser.oauth2.authentication.userdetails.UserDetailsService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * @author LISER
 * @date 2019/5/11
 */
@Slf4j
@Data
public class Oauth2AuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private UserDetailsChecker userDetailsChecker;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username, authentication);
        if (userDetails == null) {
            log.error("OAUTH2认证用户详细信息为空");
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }

        // 校验用户状态
        userDetailsChecker.check(userDetails);

        Oauth2Authentication oauth2Authentication = new Oauth2Authentication(
                userDetails.getUsername(),
                userDetails.getPassword(),
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));

        oauth2Authentication.setDetails(authentication.getDetails());

        return oauth2Authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (Oauth2Authentication.class.isAssignableFrom(authentication));
    }
}
