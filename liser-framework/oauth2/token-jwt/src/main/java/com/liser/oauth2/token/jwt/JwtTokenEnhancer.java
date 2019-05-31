package com.liser.oauth2.token.jwt;


import com.liser.oauth2.model.Oauth2Constant;
import com.liser.oauth2.model.Oauth2UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * The class Token jwt enhancer.
 *
 * @author paascloud.net @gmail.com
 */
public class JwtTokenEnhancer implements TokenEnhancer {

    /**
     * Enhance o auth 2 access token.
     *
     * @param accessToken          the access token
     * @param oAuth2Authentication the o auth 2 authentication
     * @return the o auth 2 access token
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> info = new HashMap<>(1);
        Authentication authentication = oAuth2Authentication.getUserAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Oauth2UserDetails) {
            Object principal = authentication.getPrincipal();
//            info.put(Oauth2Constant.USER_ID, ((Oauth2UserDetails) principal).getUserId());
        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);

        return accessToken;
    }

}
