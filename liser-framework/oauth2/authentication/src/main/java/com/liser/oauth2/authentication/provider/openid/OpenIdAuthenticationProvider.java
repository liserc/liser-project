package com.liser.oauth2.authentication.provider.openid;

import com.liser.oauth2.authentication.provider.AbstractRewriteAuthenticationProvider;
import com.liser.oauth2.authentication.userdetails.UserDetailsService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

/**
 * @author LISER
 * @date 2019/3/27
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OpenIdAuthenticationProvider extends AbstractRewriteAuthenticationProvider implements InitializingBean {

    private UserDetailsService userDetailsService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
    }

    @Override
    protected UserDetails loadUserDetails(String username, Authentication authentication) throws AuthenticationException {
        return userDetailsService.loadUserByUsername(username, authentication);
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        OpenIdAuthenticationToken result = new OpenIdAuthenticationToken(
                principal,
                super.getAuthoritiesMapper().mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OpenIdAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
