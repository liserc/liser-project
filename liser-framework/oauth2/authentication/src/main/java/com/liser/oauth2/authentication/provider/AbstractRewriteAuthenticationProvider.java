package com.liser.oauth2.authentication.provider;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.util.Assert;

/**
 * @author LISER
 * @date 2019/3/27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractRewriteAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private boolean forcePrincipalAsString = false;

    private UserDetailsChecker userDetailsChecker;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.userDetailsChecker, "A UserDetailsChecker must be set");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
        UserDetails userDetails = loadUserDetails(username, authentication);

        if (userDetails == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }

        // 校验用户状态
        userDetailsChecker.check(userDetails);

        Object principalToReturn = userDetails;
        if (forcePrincipalAsString) {
            principalToReturn = userDetails.getUsername();
        }

        return createSuccessAuthentication(principalToReturn, authentication, userDetails);
    }

    /**
     * 加载用户信息
     *
     * @param username       用户名
     * @param authentication 认证信息
     * @return
     * @throws AuthenticationException
     */
    protected abstract UserDetails loadUserDetails(String username, Authentication authentication) throws AuthenticationException;

    /**
     * 创建Authentication
     *
     * @param principal      主题
     * @param authentication 未认证认证信息
     * @param user           用户详情
     * @return 认证信息
     */
    protected abstract Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user);

}
