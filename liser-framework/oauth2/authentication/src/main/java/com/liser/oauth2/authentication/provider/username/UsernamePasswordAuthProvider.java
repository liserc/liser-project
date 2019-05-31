package com.liser.oauth2.authentication.provider.username;

import com.liser.oauth2.authentication.provider.AbstractRewriteAuthenticationProvider;
import com.liser.oauth2.authentication.userdetails.UserDetailsService;
import com.liser.oauth2.web.exceptions.PasswordErrorException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * @author LISER
 * @date 2019/3/27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class UsernamePasswordAuthProvider extends AbstractRewriteAuthenticationProvider implements InitializingBean {

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    private MessageSourceAccessor messages;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set");
        Assert.notNull(this.passwordEncoder, "A PasswordEncoder must be set");
        Assert.notNull(this.messages, "A MessageSourceAccessor must be set");
    }

    @Override
    protected UserDetails loadUserDetails(String username, Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username, authentication);
        String presentedPassword = authentication.getCredentials().toString();

        // 检验密码
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("Authentication failed: password does not match stored value");

            throw new PasswordErrorException(messages.getMessage(
                    "UsernamePasswordAuthenticationProvider.passwordError",
                    "Password error"));
        }

        return userDetails;
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                principal,
                authentication.getCredentials(),
                super.getAuthoritiesMapper().mapAuthorities(user.getAuthorities()));
        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
