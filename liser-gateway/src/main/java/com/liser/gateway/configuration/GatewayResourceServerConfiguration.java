package com.liser.gateway.configuration;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.liser.oauth2.model.Oauth2Authority;
import com.liser.oauth2.resource.authentication.RewriteJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * @author LISER
 * @date 2019/5/3
 */
@Configuration
@EnableWebFluxSecurity
public class GatewayResourceServerConfiguration {

    @Autowired
    private KeyPair keyPair;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/webjars/springfox-swagger-ui/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .publicKey((RSAPublicKey) keyPair.getPublic())
                .jwtAuthenticationConverter(grantedAuthoritiesExtractor());
        return http.build();
    }


    @Bean
    public RedisTemplate<String, Oauth2Authority> oauth2AuthorityRedisTemplate() {
        RedisTemplate<String, Oauth2Authority> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Oauth2Authority.class));
        return redisTemplate;
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        RewriteJwtAuthenticationConverter converter = new RewriteJwtAuthenticationConverter();
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }

}
