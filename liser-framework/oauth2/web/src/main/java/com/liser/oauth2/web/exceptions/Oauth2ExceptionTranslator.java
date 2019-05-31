package com.liser.oauth2.web.exceptions;


import com.liser.oauth2.web.model.OAuth2ErrorCode;
import com.liser.oauth2.web.model.OAuth2ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 自定义oauth2异常处理转换类
 *
 * @author LISER
 * @date 2019/2/25
 */
@Slf4j
public class Oauth2ExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {

    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);

        Exception ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);

        // OAuth2异常
        if (ase != null) {
            Oauth2AuthorizationException authorizationException = translateOAuth2Exception((OAuth2Exception) ase);
            return handleOAuth2Exception(authorizationException);
        }

        // 认证异常
        ase = (AuthenticationException) throwableAnalyzer
                .getFirstThrowableOfType(AuthenticationException.class,
                        causeChain);
        if (ase != null) {
            Oauth2AuthorizationException authenticationException = translateAuthenticationException((AuthenticationException) ase);
            return handleOAuth2Exception(authenticationException);
        }

        // 拒绝访问异常
        ase = (AccessDeniedException) throwableAnalyzer
                .getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (ase != null) {
            Oauth2ForbiddenException forbiddenException = new Oauth2ForbiddenException(ase.getMessage(), ase);
            forbiddenException.setErrorCode(OAuth2ErrorStatus.no_access.getCode());
            forbiddenException.setErrorMessage(OAuth2ErrorStatus.no_access.getMessage());
            return handleOAuth2Exception(forbiddenException);
        }

        // 系统内部错误异常
        return handleOAuth2Exception(
                new Oauth2ServerErrorException(e.getMessage(), e)
        );
    }

    /**
     * oauth2错误码转换为自定义返回信息
     *
     * @param ex OAuth2Exception异常
     * @return
     */
    Oauth2AuthorizationException translateOAuth2Exception(OAuth2Exception ex) {
        int httpErrorCode = ex.getHttpErrorCode();
        String oAuth2ErrorCode = ex.getOAuth2ErrorCode();

        ReceiveOauth2Exception exception = new ReceiveOauth2Exception(ex.getMessage(), ex);
        exception.setHttpErrorCode(httpErrorCode);
        exception.setOAuth2ErrorCode(oAuth2ErrorCode);

        if (!StringUtils.isEmpty(oAuth2ErrorCode)) {
            OAuth2ErrorCode errorStatus = OAuth2ErrorCode.valueOf(oAuth2ErrorCode);
            exception.setErrorCode(errorStatus.getCode());
            exception.setErrorMessage(errorStatus.getMessage());
        } else {
            exception.setErrorCode(OAuth2ErrorStatus.no_access.getCode());
            exception.setErrorMessage(OAuth2ErrorStatus.no_access.getMessage());
        }

        return exception;
    }

    Oauth2AuthorizationException translateAuthenticationException(AuthenticationException ex) {
        Oauth2UnauthorizedException unauthorizedException = new Oauth2UnauthorizedException(ex.getMessage(), ex);

        if (ex instanceof AccessTokenRequiredException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.access_token_required.getCode());
            unauthorizedException.setErrorMessage(ex.getMessage());

        } else if (ex instanceof UsernameNotFoundException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.username_not_found.getCode());
            unauthorizedException.setErrorMessage(ex.getMessage());

        } else if (ex instanceof PasswordErrorException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.password_error.getCode());
            unauthorizedException.setErrorMessage(ex.getMessage());

        } else if (ex instanceof LockedException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.locked.getCode());
            unauthorizedException.setErrorMessage(ex.getMessage());

        } else if (ex instanceof DisabledException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.disabled.getCode());
            unauthorizedException.setErrorMessage(ex.getMessage());

        } else if (ex instanceof AccountExpiredException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.account_expired.getCode());
            unauthorizedException.setErrorMessage(ex.getMessage());

        } else if (ex instanceof CredentialsExpiredException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.credentials_expired.getCode());
            unauthorizedException.setErrorMessage(ex.getMessage());

        } else if (ex instanceof InsufficientAuthenticationException) {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.no_access.getCode());
            unauthorizedException.setErrorMessage(OAuth2ErrorStatus.no_access.getMessage());

        } else {
            unauthorizedException.setErrorCode(OAuth2ErrorStatus.unknown_anomaly.getCode());
            unauthorizedException.setErrorMessage(OAuth2ErrorStatus.unknown_anomaly.getMessage());

        }

        return unauthorizedException;
    }

    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(Oauth2AuthorizationException exception) throws IOException {
        int status = exception.getHttpErrorCode();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");

        if (status == HttpStatus.UNAUTHORIZED.value()) {
            headers.set("WWW-Authenticate", String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, exception.getSummary()));
        }

        log.error("OAuth2错误码：{} ->异常信息：{}", exception.getOAuth2ErrorCode(), exception.getMessage());

        return new ResponseEntity<>(exception, headers, HttpStatus.valueOf(status));
    }

    public void setThrowableAnalyzer(ThrowableAnalyzer throwableAnalyzer) {
        this.throwableAnalyzer = throwableAnalyzer;
    }

}
