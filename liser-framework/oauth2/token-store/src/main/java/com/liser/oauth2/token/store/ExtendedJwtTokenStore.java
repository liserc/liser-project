package com.liser.oauth2.token.store;

import com.liser.oauth2.token.jwt.ExtendedJwtAccessTokenConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.*;

/**
 * 拓展的jwt令牌存储（加入自己的业务逻辑）
 *
 * @author LISER
 * @date 2019/4/12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class ExtendedJwtTokenStore implements TokenStore {

    private Oauth2TokenStore oauth2TokenStore;

    private ExtendedJwtAccessTokenConverter jwtTokenEnhancer;

    private ApprovalStore approvalStore;

    /**
     * Create a JwtTokenStore with this token enhancer (should be shared with the DefaultTokenServices if used).
     *
     * @param jwtTokenEnhancer
     */
    public ExtendedJwtTokenStore(ExtendedJwtAccessTokenConverter jwtTokenEnhancer) {
        this.jwtTokenEnhancer = jwtTokenEnhancer;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        Map<String, Object> objectMap = jwtTokenEnhancer.decode(token);
        if (objectMap.containsKey(AccessTokenConverter.JTI)) {
            String jti = objectMap.get(AccessTokenConverter.JTI).toString();

            AccessTokenWrapper accessTokenWrapper = oauth2TokenStore.readAccessToken(jti);
            if (accessTokenWrapper == null) {
                throw new InvalidGrantException("访问令牌已过期: " + jti);
            }

            /// 缓存权限信息（目前不需要）
            /*AuthenticationWrapper authenticationWrapper = oauth2TokenStore.readOAuth2Authentication(accessTokenWrapper.getJti());
            if (authenticationWrapper == null) {
                throw new InvalidGrantException("权限信息为空：" + accessTokenWrapper.getJti());
            }

            List<Oauth2Authority> authorities = authenticationWrapper.getAuthorities();

            return jwtTokenEnhancer.extractAuthentication(authorities, objectMap);*/
        }

        return jwtTokenEnhancer.extractAuthentication(objectMap);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        OAuth2RefreshToken refreshToken = token.getRefreshToken();
        if (refreshToken != null) {
            OAuth2AccessToken accessToken = convertAccessToken(refreshToken.getValue());
            String ati = accessToken.getAdditionalInformation().get(AccessTokenConverter.ATI).toString();
            Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
            additionalInformation.put(AccessTokenConverter.ATI, ati);
            ((DefaultOAuth2AccessToken) token).setAdditionalInformation(additionalInformation);
            oauth2TokenStore.storeAccessToken(token, authentication);
        }
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = convertAccessToken(tokenValue);
        if (jwtTokenEnhancer.isRefreshToken(accessToken)) {
            throw new InvalidTokenException("Encoded token is a refresh token");
        }

        return accessToken;
    }

    private OAuth2AccessToken convertAccessToken(String tokenValue) {
        return jwtTokenEnhancer.extractAccessToken(tokenValue, jwtTokenEnhancer.decode(tokenValue));
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        OAuth2Authentication authentication = readAuthentication(token);
        if (authentication == null) {
            return;
        }
        oauth2TokenStore.removeAccessToken(authentication);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
//        OAuth2AccessToken encodedRefreshToken = convertAccessToken(refreshToken.getValue());
//        oauth2TokenStore.storeOAuth2Authentication(encodedRefreshToken, authentication);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        OAuth2AccessToken encodedRefreshToken = convertAccessToken(tokenValue);
        OAuth2RefreshToken refreshToken = createRefreshToken(encodedRefreshToken);
        if (approvalStore != null) {
            OAuth2Authentication authentication = readAuthentication(tokenValue);
            if (authentication.getUserAuthentication() != null) {
                String userId = authentication.getUserAuthentication().getName();
                String clientId = authentication.getOAuth2Request().getClientId();
                Collection<Approval> approvals = approvalStore.getApprovals(userId, clientId);
                Collection<String> approvedScopes = new HashSet<>();
                for (Approval approval : approvals) {
                    if (approval.isApproved()) {
                        approvedScopes.add(approval.getScope());
                    }
                }
                if (!approvedScopes.containsAll(authentication.getOAuth2Request().getScope())) {
                    return null;
                }
            }
        }
        return refreshToken;
    }

    private OAuth2RefreshToken createRefreshToken(OAuth2AccessToken encodedRefreshToken) {
        if (!jwtTokenEnhancer.isRefreshToken(encodedRefreshToken)) {
            throw new InvalidTokenException("Encoded token is not a refresh token");
        }

        if (encodedRefreshToken.getExpiration() != null) {
            return new DefaultExpiringOAuth2RefreshToken(encodedRefreshToken.getValue(),
                    encodedRefreshToken.getExpiration());
        }

        return new DefaultOAuth2RefreshToken(encodedRefreshToken.getValue());
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
//        Map<String, Object> objectMap = jwtTokenEnhancer.decode(token.getValue());
//        if (objectMap.containsKey(AccessTokenConverter.JTI)) {
//            String jti = objectMap.get(AccessTokenConverter.JTI).toString();
//            AuthenticationWrapper authenticationWrapper = oauth2TokenStore.readOAuth2Authentication(jti);
//            if (authenticationWrapper == null) {
//                log.debug("刷新令牌无效或已过期：{}", token.getValue());
//                throw new InvalidTokenException("刷新令牌无效或已过期: " + token.getValue());
//            }
//
//            // 权限集合
//            List<Oauth2Authority> authorities = authenticationWrapper.getAuthorities();
//
//            return jwtTokenEnhancer.extractAuthentication(authorities, objectMap);
//        }

        return readAuthentication(token.getValue());
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        remove(token.getValue());
//        OAuth2AccessToken accessToken = convertAccessToken(token.getValue());
//        oauth2TokenStore.removeOauth2Authentication(accessToken);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {

    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        // 在这里删除jwt令牌存储信息
        oauth2TokenStore.removeAccessToken(authentication);
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return Collections.emptySet();
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return Collections.emptySet();
    }

    private void remove(String token) {
        if (approvalStore != null) {
            OAuth2Authentication auth = readAuthentication(token);
            String clientId = auth.getOAuth2Request().getClientId();
            Authentication user = auth.getUserAuthentication();
            if (user != null) {
                Collection<Approval> approvals = new ArrayList<>();
                for (String scope : auth.getOAuth2Request().getScope()) {
                    approvals.add(new Approval(user.getName(), clientId, scope, new Date(), Approval.ApprovalStatus.APPROVED));
                }
                approvalStore.revokeApprovals(approvals);
            }
        }
    }

}
