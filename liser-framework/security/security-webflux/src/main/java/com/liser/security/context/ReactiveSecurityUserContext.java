package com.liser.security.context;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.liser.oauth2.model.SecurityUser;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * @author LISER
 * @date 2019/5/25
 */
@UtilityClass
public class ReactiveSecurityUserContext {

    public Mono<SecurityUser> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.error(new IllegalStateException("ReactiveSecurityContext is empty")))
                .map(SecurityContext::getAuthentication)
                .cast(JwtAuthenticationToken.class)
                .map(ReactiveSecurityUserContext::securityUser);
    }

    private SecurityUser securityUser(JwtAuthenticationToken authenticationToken) {

        // 权限集
        List<Integer> authorities = Lists.newArrayList();
        Collection<GrantedAuthority> grantedAuthorities = authenticationToken.getAuthorities();
        grantedAuthorities.forEach(grantedAuthority -> {
            String authority = grantedAuthority.getAuthority();
            List<Integer> authorityList = JSON.parseArray(authority, Integer.class);
            authorities.addAll(authorityList);
        });

        // 构造返回结果
        return SecurityUser.builder()
                .userId(Long.parseLong(authenticationToken.getName()))
                .authorities(authorities)
                .build();
    }
}
