package com.liser.oauth2.resource.configuration;

import com.liser.oauth2.resource.authentication.RewriteJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LISER
 * @date 2019/5/14
 */
@Configuration
public class Oauth2ResourceServerAutoConfiguration {

    @Bean
    public RewriteJwtAuthenticationConverter grantedAuthoritiesConverter() {
        return new RewriteJwtAuthenticationConverter();
    }
}
