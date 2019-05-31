package com.liser.oauth2.web.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author LISER
 */
public class CaptchaErrorException extends AuthenticationException {

    public CaptchaErrorException(String msg) {
        super(msg);
    }

    public CaptchaErrorException(String msg, Throwable t) {
        super(msg, t);
    }
}
