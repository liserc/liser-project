package com.liser.oauth2.web.exceptions;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liser.oauth2.web.model.OAuth2ErrorCode;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author LISER
 * @date 2019/2/26
 */
@JsonSerialize(using = Oauth2AuthorizationExceptionJacksonSerializer.class)
public class Oauth2AuthorizationException extends OAuth2Exception {

    /**
     * 系统自定义错误码
     */
    private int errorCode = OAuth2ErrorCode.invalid_request.getCode();

    /**
     * 错误提示信息
     */
    private String errorMessage = OAuth2ErrorCode.invalid_request.getMessage();

    public Oauth2AuthorizationException(String msg, Throwable t) {
        super(msg, t);
    }

    public Oauth2AuthorizationException(String msg) {
        super(msg);
    }

    @Override
    public int getHttpErrorCode() {
        return 400;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_request";
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
