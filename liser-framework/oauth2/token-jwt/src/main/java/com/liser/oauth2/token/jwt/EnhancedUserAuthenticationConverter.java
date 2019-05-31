package com.liser.oauth2.token.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @author LISER
 * @date 2019/4/14
 */
public interface EnhancedUserAuthenticationConverter {

    /**
     * 增强的创建Authentication方法
     *
     * @param authorities 权限集合
     * @param map         附加信息
     * @return 认证信息
     */
    Authentication extractAuthentication(Collection<? extends GrantedAuthority> authorities, Map<String, ?> map);
}
