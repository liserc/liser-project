package com.liser.oauth2.web.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author LISER
 * @date 2019/3/27
 */
public class Oauth2ForbiddenException extends Oauth2AuthorizationException {

    public Oauth2ForbiddenException(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.FORBIDDEN.value();
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "access_denied";
    }
}
