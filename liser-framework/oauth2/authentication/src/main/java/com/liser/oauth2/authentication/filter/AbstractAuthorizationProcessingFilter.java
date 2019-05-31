package com.liser.oauth2.authentication.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 抽象的授权处理过滤器
 *
 * @author LISER
 * @date 2019/1/25
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractAuthorizationProcessingFilter extends OncePerRequestFilter {

    private RequestMatcher requiresAuthenticationRequestMatcher;

    /**
     * 失败处理器
     */
    private AuthenticationFailureHandler failureHandler;

    /**
     * 默认关掉验证
     */
    private boolean enabled = false;

    protected AbstractAuthorizationProcessingFilter(
            RequestMatcher requiresAuthenticationRequestMatcher) {
        Assert.notNull(requiresAuthenticationRequestMatcher,
                "requiresAuthenticationRequestMatcher cannot be null");
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        if (requiresAuthentication(request, response)) {
            if (enabled){
                try {
                    attemptAuthentication(request, response);
                } catch (AuthenticationException fail) {
                    failureHandler.onAuthenticationFailure(request, response, fail);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    /**
     * 具体过滤规则
     *
     * @param request  请求
     * @param response 响应
     * @throws AuthenticationException 认证异常
     */
    public abstract void attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

}
