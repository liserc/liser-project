package com.liser.oauth2.web.exceptions;

import org.springframework.http.HttpStatus;

/**
 * @author LISER
 * @date 2019/3/27
 */
public class Oauth2ServerErrorException extends Oauth2AuthorizationException {

    public Oauth2ServerErrorException(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "server_error";
    }
}
