package com.liser.oauth2.web.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author LISER
 * @date 2019/3/27
 */
public class Oauth2MethodNotAllowed extends Oauth2AuthorizationException {

    public Oauth2MethodNotAllowed(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.METHOD_NOT_ALLOWED.value();
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "method_not_allowed";
    }
}
