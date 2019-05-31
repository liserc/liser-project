package com.liser.oauth2.token.store;

import com.alibaba.fastjson.JSON;
import com.liser.oauth2.model.Oauth2Authority;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author LISER
 * @date 2019/5/6
 */
@Data
@Slf4j
public class DefaultOauth2TokenStore implements Oauth2TokenStore {

    private static final String AUTH_TO_ACCESS = "auth_to_access:";

    private static final String ACCESS = "access:";

    private static final String AUTH = "auth:";

    private String prefix = "";

    private RedisTemplate<String, AuthToAccessWrapper> authToAccessTemplate;

    private RedisTemplate<String, AccessTokenWrapper> accessTokenTemplate;

    private RedisTemplate<String, AuthenticationWrapper> authenticationTemplate;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    @Override
    public void removeTokenStore(OAuth2Authentication authentication) {
        String extractKey = authenticationKeyGenerator.extractKey(authentication);
        String authToAccess = redisKey(AUTH_TO_ACCESS + extractKey);

        Optional.ofNullable(authToAccessTemplate.opsForValue().get(authToAccess))
                .map((wrapper) -> {
                    authToAccessTemplate.delete(authToAccess);
                    return redisKey(ACCESS + wrapper.getJti());
                })
                .flatMap((redisKey) -> {
                    AccessTokenWrapper accessTokenWrapper = accessTokenTemplate.opsForValue().get(redisKey);
                    accessTokenTemplate.delete(redisKey);
                    return Optional.ofNullable(accessTokenWrapper);
                })
                .map(AccessTokenWrapper::getJti)
                .map(jti -> redisKey(AUTH + jti))
                .map(authenticationTemplate::delete);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
        // OAuth2AccessToken里面包含OAuth2RefreshToken所以jti被OAuth2RefreshToken所覆盖，
        // 需要手动将OAuth2AccessToken的令牌唯一标识添加进来（ati就是OAuth2AccessToken的唯一标识）
        String ati = oAuth2AccessToken.getAdditionalInformation().get(AccessTokenConverter.ATI).toString();
        if (StringUtils.isEmpty(ati)) {
            return;
        }

        // 认证信息关联访问令牌
        String extractKey = authenticationKeyGenerator.extractKey(authentication);
        String authToAccess = redisKey(AUTH_TO_ACCESS + extractKey);
        AuthToAccessWrapper authToAccessWrapper = new AuthToAccessWrapper();
        authToAccessWrapper.setJti(ati);

        // 访问令牌关联认证的权限信息
        String jti = oAuth2AccessToken.getAdditionalInformation().get(AccessTokenConverter.JTI).toString();
        String access = redisKey(ACCESS + ati);
        AccessTokenWrapper accessTokenWrapper = new AccessTokenWrapper();
        accessTokenWrapper.setJti(jti);

        authToAccessTemplate.opsForValue().set(authToAccess, authToAccessWrapper);
        accessTokenTemplate.opsForValue().set(access, accessTokenWrapper);
        if (oAuth2AccessToken.getExpiration() != null) {
            authToAccessTemplate.boundValueOps(authToAccess).expire(oAuth2AccessToken.getExpiresIn(), TimeUnit.SECONDS);
            accessTokenTemplate.boundValueOps(access).expire(oAuth2AccessToken.getExpiresIn(), TimeUnit.SECONDS);
        }
    }

    @Override
    public AccessTokenWrapper readAccessToken(String jti) {
        String redisKey = redisKey(ACCESS + jti);
        return accessTokenTemplate.opsForValue().get(redisKey);
    }

    @Override
    public void removeAccessToken(OAuth2Authentication auth2Authentication) {
        String extractKey = authenticationKeyGenerator.extractKey(auth2Authentication);
        String authToAccess = redisKey(AUTH_TO_ACCESS + extractKey);

        Optional.ofNullable(authToAccessTemplate.opsForValue().get(authToAccess))
                .map((wrapper) -> {
                    authToAccessTemplate.delete(authToAccess);
                    return redisKey(ACCESS + wrapper.getJti());
                })
                .map((redisKey) -> accessTokenTemplate.delete(redisKey));
    }

    @Override
    public void storeOAuth2Authentication(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication) {
        OAuth2Request oAuth2Request = authentication.getOAuth2Request();
        Authentication userAuthentication = authentication.getUserAuthentication();
        String jti = oAuth2AccessToken.getAdditionalInformation().get(AccessTokenConverter.JTI).toString();

        AuthenticationWrapper oauth2Authentication = new AuthenticationWrapper();
        oauth2Authentication.setGrantType(oAuth2Request.getGrantType());
        oauth2Authentication.setClientId(oAuth2Request.getClientId());

        Collection<? extends GrantedAuthority> authorities;
        // 客户端模式没有userAuthentication
        if (userAuthentication == null) {
            authorities = authentication.getAuthorities();
            oauth2Authentication.setUsername(authentication.getName());

        } else {
            authorities = userAuthentication.getAuthorities();
            oauth2Authentication.setUsername(userAuthentication.getName());
        }

        convertAuthorities(authorities, oauth2Authentication);
        String redisKey = redisKey(jti);
        authenticationTemplate.opsForValue().set(redisKey, oauth2Authentication);
        if (oAuth2AccessToken.getExpiration() != null) {
            authenticationTemplate.boundValueOps(redisKey).expire(oAuth2AccessToken.getExpiresIn(), TimeUnit.SECONDS);
        }
    }

    @Override
    public AuthenticationWrapper readOAuth2Authentication(String jti) {
        String redisKey = redisKey(AUTH + jti);
        return authenticationTemplate.opsForValue().get(redisKey);
    }

    @Override
    public void removeOauth2Authentication(OAuth2AccessToken accessToken) {
        if (accessToken.getAdditionalInformation().containsKey(AccessTokenConverter.JTI)) {
            String jti = accessToken.getAdditionalInformation().get(AccessTokenConverter.JTI).toString();
            String redisKey = redisKey(AUTH + jti);
            authenticationTemplate.delete(redisKey);
        }
    }

    @Override
    public Collection<GrantedAuthority> readAuthorities(String jti) {
        return Optional.ofNullable(readAccessToken(jti))
                .map(a -> readOAuth2Authentication(a.getJti()))
                .map(AuthenticationWrapper::getAuthorities)
                .orElse(Collections.emptyList())
                .stream()
                .map(JSON::toJSONString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private String redisKey(String token) {
        if (StringUtils.isEmpty(prefix)) {
            return token;
        }
        return prefix + token;
    }

    private void convertAuthorities(Collection<? extends GrantedAuthority> authorities, AuthenticationWrapper oauth2Authentication) {
        if (!CollectionUtils.isEmpty(authorities)) {
            Set<String> authoritySet = AuthorityUtils.authorityListToSet(authorities);
            List<Oauth2Authority> oauth2Authorities = authoritySet
                    .stream()
                    .map(s -> JSON.parseObject(s, Oauth2Authority.class))
                    .collect(Collectors.toList());

            oauth2Authentication.setAuthorities(oauth2Authorities);
        }
    }
}
