package com.liser.oauth2.web.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * @author LISER
 * @date 2019/4/15
 */
public class PasswordErrorException extends AuthenticationException {

    public PasswordErrorException(String msg, Throwable t) {
        super(msg, t);
    }

    public PasswordErrorException(String msg) {
        super(msg);
    }
}
