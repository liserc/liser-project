package com.liser.oauth2.token.configuration;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.liser.oauth2.token.jwt.ExtendedJwtAccessTokenConverter;
import com.liser.oauth2.token.jwt.ExtractAccessTokenConverter;
import com.liser.oauth2.token.jwt.ExtractUserAuthenticationConverter;
import com.liser.oauth2.token.store.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author LISER
 * @date 2019/5/6
 */
@Configuration
public class Oauth2TokenAutoConfiguration {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    KeyPair keyPair() {
        try {
            String privateExponent = "3851612021791312596791631935569878540203393691253311342052463788814433805390794604753109719790052408607029530149004451377846406736413270923596916756321977922303381344613407820854322190592787335193581632323728135479679928871596911841005827348430783250026013354350760878678723915119966019947072651782000702927096735228356171563532131162414366310012554312756036441054404004920678199077822575051043273088621405687950081861819700809912238863867947415641838115425624808671834312114785499017269379478439158796130804789241476050832773822038351367878951389438751088021113551495469440016698505614123035099067172660197922333993";
            String modulus = "18044398961479537755088511127417480155072543594514852056908450877656126120801808993616738273349107491806340290040410660515399239279742407357192875363433659810851147557504389760192273458065587503508596714389889971758652047927503525007076910925306186421971180013159326306810174367375596043267660331677530921991343349336096643043840224352451615452251387611820750171352353189973315443889352557807329336576421211370350554195530374360110583327093711721857129170040527236951522127488980970085401773781530555922385755722534685479501240842392531455355164896023070459024737908929308707435474197069199421373363801477026083786683";
            String exponent = "65537";

            RSAPublicKeySpec publicSpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(exponent));
            RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return new KeyPair(factory.generatePublic(publicSpec), factory.generatePrivate(privateSpec));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Jwt访问令牌转换器
     *
     * @return the jwt access token converter
     */
    @Bean
    public ExtendedJwtAccessTokenConverter extendedJwtAccessTokenConverter() {
        // 自定义jwt令牌返回的携带信息
        ExtractUserAuthenticationConverter userAuthenticationConverter = new ExtractUserAuthenticationConverter();

        ExtractAccessTokenConverter accessTokenConverter = new ExtractAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
        accessTokenConverter.setEnhancedUserAuthenticationConverter(userAuthenticationConverter);

        ExtendedJwtAccessTokenConverter converter = new ExtendedJwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        converter.setAccessTokenConverter(accessTokenConverter);
        converter.setExtendedAccessTokenConverter(accessTokenConverter);

        return converter;
    }

    /**
     * token增强器（添加自定义token携带信息）
     *
     * @return the token enhancer
     */
//    @Bean
////    @ConditionalOnBean(TokenEnhancer.class)
////    public TokenEnhancer jwtTokenEnhancer() {
////        return new JwtTokenEnhancer();
////    }

    /**
     * 令牌存储
     *
     * @return the token store
     */
    @Bean
    public TokenStore extendedJwtTokenStore() {
        ExtendedJwtTokenStore tokenStore = new ExtendedJwtTokenStore(extendedJwtAccessTokenConverter());
        tokenStore.setOauth2TokenStore(oauth2TokenStore());
        return tokenStore;
    }

    /**
     * 真正进行令牌存储的处理类
     *
     * @return
     */
    @Bean
    public Oauth2TokenStore oauth2TokenStore() {
        DefaultOauth2TokenStore oauth2TokenStore = new DefaultOauth2TokenStore();
        oauth2TokenStore.setAuthToAccessTemplate(authToAccessTokenWrapperRedisTemplate());
        oauth2TokenStore.setAccessTokenTemplate(accessTokenWrapperRedisTemplate());
        oauth2TokenStore.setAuthenticationTemplate(authenticationWrapperRedisTemplate());
        return oauth2TokenStore;
    }

    @Bean
    public RedisTemplate<String, AuthToAccessWrapper> authToAccessTokenWrapperRedisTemplate() {
        RedisTemplate<String, AuthToAccessWrapper> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(AuthToAccessWrapper.class));
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, AccessTokenWrapper> accessTokenWrapperRedisTemplate() {
        RedisTemplate<String, AccessTokenWrapper> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(AccessTokenWrapper.class));
        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, AuthenticationWrapper> authenticationWrapperRedisTemplate() {
        RedisTemplate<String, AuthenticationWrapper> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(AuthenticationWrapper.class));
        return redisTemplate;
    }
}
