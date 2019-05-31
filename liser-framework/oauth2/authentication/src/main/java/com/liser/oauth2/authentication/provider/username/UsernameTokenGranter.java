package com.liser.oauth2.authentication.provider.username;

import com.liser.oauth2.authentication.provider.Oauth2Authentication;
import com.liser.oauth2.model.Oauth2Constant;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LISER
 * @date 2019/3/8
 */
public class UsernameTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = Oauth2Constant.PASSWORD;

    private final AuthenticationManager authenticationManager;

    public UsernameTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    protected UsernameTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                   ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String username = parameters.get("username");
        String password = parameters.get("password");
        // Protect from downstream leaks of password
        parameters.remove("password");

//        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        Authentication userAuth = new Oauth2Authentication(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(tokenRequest);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException ase) {
            //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
            throw new InvalidGrantException(ase.getMessage());
        }

        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);

        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
