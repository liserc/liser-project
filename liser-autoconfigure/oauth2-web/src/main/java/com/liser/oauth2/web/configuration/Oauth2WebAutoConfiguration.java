package com.liser.oauth2.web.configuration;

import com.liser.oauth2.web.exceptions.Oauth2ExceptionTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * @author LISER
 * @date 2019/5/14
 */
@Configuration
public class Oauth2WebAutoConfiguration {

    @Bean
    public WebResponseExceptionTranslator oauth2WebResponseExceptionTranslator() {
        return new Oauth2ExceptionTranslator();
    }

    @Bean
    public AuthenticationEntryPoint oAuth2AuthenticationEntryPoint(WebResponseExceptionTranslator oauth2WebResponseExceptionTranslator) {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setExceptionTranslator(oauth2WebResponseExceptionTranslator);
        return authenticationEntryPoint;
    }

    @Bean
    public AccessDeniedHandler oAuth2AccessDeniedHandler(WebResponseExceptionTranslator oauth2WebResponseExceptionTranslator) {
        OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        accessDeniedHandler.setExceptionTranslator(oauth2WebResponseExceptionTranslator);
        return accessDeniedHandler;
    }

}
