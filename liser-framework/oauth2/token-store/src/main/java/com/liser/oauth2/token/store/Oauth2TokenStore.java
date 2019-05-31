package com.liser.oauth2.token.store;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Collection;

/**
 * @author LISER
 * @date 2019/5/6
 */
public interface Oauth2TokenStore {

    /**
     * 清空令牌信息
     *
     * @param authentication 认证信息
     */
    void removeTokenStore(OAuth2Authentication authentication);

    /**
     * 缓存访问令牌信息
     *
     * @param oAuth2AccessToken 访问令牌
     * @param authentication    认证信息
     */
    void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication);

    /**
     * 读取访问令牌
     *
     * @param jti 令牌唯一标识
     * @return 访问令牌包装器
     */
    AccessTokenWrapper readAccessToken(String jti);

    /**
     * 移除访问令牌
     *
     * @param auth2Authentication 认证信息
     */

    void removeAccessToken(OAuth2Authentication auth2Authentication);


    /**
     * 存储权限信息
     *
     * @param oAuth2AccessToken 访问令牌
     * @param authentication    认证信息
     */
    void storeOAuth2Authentication(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication);

    /**
     * 加载权限信息
     *
     * @param jti 令牌唯一标识
     * @return 权限包装器
     */
    AuthenticationWrapper readOAuth2Authentication(String jti);

    /**
     * 移除权限信息
     *
     * @param accessToken 访问令牌
     */
    void removeOauth2Authentication(OAuth2AccessToken accessToken);

    /**
     * 读取存储的权限信息
     *
     * @param jti 访问令牌唯一标识
     * @return
     */
    Collection<GrantedAuthority> readAuthorities(String jti);
}
