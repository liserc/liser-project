package com.liser.oauth2.token.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.*;

/**
 * @author LISER
 * @date 2019/4/14
 */
public class ExtractAccessTokenConverter extends DefaultAccessTokenConverter implements EnhancedAccessTokenConverter {

    private EnhancedUserAuthenticationConverter enhancedUserAuthenticationConverter;

    private boolean includeGrantType;

    public void setEnhancedUserAuthenticationConverter(EnhancedUserAuthenticationConverter enhancedUserAuthenticationConverter) {
        this.enhancedUserAuthenticationConverter = enhancedUserAuthenticationConverter;
    }

    @Override
    public void setIncludeGrantType(boolean includeGrantType) {
        super.setIncludeGrantType(includeGrantType);
        this.includeGrantType = includeGrantType;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Collection<? extends GrantedAuthority> authorities, Map<String, ?> map) {
        Map<String, String> parameters = new HashMap<>(16);
        Set<String> scope = extractScope(map);
        Authentication user = enhancedUserAuthenticationConverter.extractAuthentication(authorities, map);
        String clientId = (String) map.get(CLIENT_ID);
        parameters.put(CLIENT_ID, clientId);

        if (includeGrantType && map.containsKey(GRANT_TYPE)) {
            parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));
        }

        Set<String> resourceIds = new LinkedHashSet<>(map.containsKey(AUD) ? getAudience(map) : Collections.emptySet());

        if (user == null && map.containsKey(AUTHORITIES)) {
            @SuppressWarnings("unchecked")
            String[] roles = ((Collection<String>) map.get(AUTHORITIES)).toArray(new String[0]);
            authorities = AuthorityUtils.createAuthorityList(roles);
        }

        OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, null, null, null);

        return new OAuth2Authentication(request, user);
    }

    private Collection<String> getAudience(Map<String, ?> map) {
        Object auds = map.get(AUD);
        if (auds instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<String> result = (Collection<String>) auds;
            return result;
        }
        return Collections.singleton((String) auds);
    }

    private Set<String> extractScope(Map<String, ?> map) {
        Set<String> scope = Collections.emptySet();
        if (map.containsKey(SCOPE)) {
            Object scopeObj = map.get(SCOPE);
            if (scopeObj instanceof String) {
                scope = new LinkedHashSet<String>(Arrays.asList(((String) scopeObj).split(" ")));
            } else if (Collection.class.isAssignableFrom(scopeObj.getClass())) {
                @SuppressWarnings("unchecked")
                Collection<String> scopeColl = (Collection<String>) scopeObj;
                // Preserve ordering
                scope = new LinkedHashSet<String>(scopeColl);
            }
        }
        return scope;
    }
}
