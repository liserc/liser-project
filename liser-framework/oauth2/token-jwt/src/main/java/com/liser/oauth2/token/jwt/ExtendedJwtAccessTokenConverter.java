package com.liser.oauth2.token.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Collection;
import java.util.Map;

/**
 * @author LISER
 * @date 2019/4/14
 */
public class ExtendedJwtAccessTokenConverter extends JwtAccessTokenConverter implements EnhancedAccessTokenConverter {

    private EnhancedAccessTokenConverter extendedAccessTokenConverter;

    public void setExtendedAccessTokenConverter(EnhancedAccessTokenConverter extendedAccessTokenConverter) {
        this.extendedAccessTokenConverter = extendedAccessTokenConverter;
    }

    @Override
    public Map<String, Object> decode(String token) {
        return super.decode(token);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Collection<? extends GrantedAuthority> authorities, Map<String, ?> map) {
        return extendedAccessTokenConverter.extractAuthentication(authorities, map);
    }
}
