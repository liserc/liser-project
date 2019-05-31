package com.liser.oauth2.token.jwt;

import com.liser.oauth2.model.Oauth2Constant;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LISER
 * @date 2019/4/13
 */
public class ExtractUserAuthenticationConverter implements UserAuthenticationConverter, EnhancedUserAuthenticationConverter {

    private Collection<? extends GrantedAuthority> defaultAuthorities;

    private String subject = Oauth2Constant.SUBJECT;

    private String authorities = Oauth2Constant.AUTHORITIES;

    public void setDefaultAuthorities(String[] defaultAuthorities) {
        this.defaultAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.arrayToCommaDelimitedString(defaultAuthorities));
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<>(1);
        response.put(subject, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(authorities, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }

        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(subject)) {
            Object principal = map.get(subject);
            Collection<? extends GrantedAuthority> authorities = this.getAuthorities(map);
            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        } else {
            return null;
        }
    }

    @Override
    public Authentication extractAuthentication(Collection<? extends GrantedAuthority> authorities, Map<String, ?> map) {
        if (map.containsKey(subject)) {
            Object principal = map.get(subject);
            return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(authorities)) {
            return this.defaultAuthorities;
        } else {
            Object authorities = map.get(this.authorities);
            if (authorities instanceof String) {
                return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
            } else if (authorities instanceof Collection) {
                return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.collectionToCommaDelimitedString((Collection) authorities));
            } else {
                throw new IllegalArgumentException("Authorities must be either a String or a Collection");
            }
        }
    }
}
