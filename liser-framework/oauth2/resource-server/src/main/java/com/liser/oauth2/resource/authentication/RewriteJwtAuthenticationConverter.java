package com.liser.oauth2.resource.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.liser.oauth2.model.Oauth2Constant.AUTHORITIES;

/**
 * 从令牌中读取到访问令牌拥有的权限信息
 *
 * @author LISER
 * @date 2019/5/5
 */
public class RewriteJwtAuthenticationConverter extends JwtAuthenticationConverter {

    public RewriteJwtAuthenticationConverter() {
    }

    @Override
    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<String> authorities = getAuthorities(jwt);
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getAuthorities(Jwt jwt) {
        Object authorityList = jwt.getClaims().get(AUTHORITIES);
        if (authorityList instanceof Collection) {
            return (Collection<String>) authorityList;
        }
        return Collections.emptyList();
    }
}
