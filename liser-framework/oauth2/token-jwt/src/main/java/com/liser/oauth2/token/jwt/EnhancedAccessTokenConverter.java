package com.liser.oauth2.token.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collection;
import java.util.Map;

/**
 * @author LISER
 * @date 2019/4/14
 */
public interface EnhancedAccessTokenConverter {

    /**
     * 拓展OAuth2Authentication创建过程
     *
     * @param authorities 权限集合
     * @param map         附加信息
     * @return 认证信息
     */
    OAuth2Authentication extractAuthentication(Collection<? extends GrantedAuthority> authorities, Map<String, ?> map);
}
