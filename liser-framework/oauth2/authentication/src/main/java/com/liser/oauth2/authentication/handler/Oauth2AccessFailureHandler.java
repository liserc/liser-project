package com.liser.oauth2.authentication.handler;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2访问失败处理器
 *
 * @author LISER
 * @date 2018/11/23 0:12
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class Oauth2AccessFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authentication) throws IOException, ServletException {
        authenticationEntryPoint.commence(request, response, authentication);
    }
}
