package com.liser.oauth2.web.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * OAuth2Exception异常接收类
 *
 * @author LISER
 * @date 2019/4/11
 */
@Setter
@EqualsAndHashCode(callSuper = false)
public class ReceiveOauth2Exception extends Oauth2AuthorizationException {

    private int httpErrorCode;

    private String oAuth2ErrorCode;

    public ReceiveOauth2Exception(String msg, Throwable t) {
        super(msg, t);
    }

    public ReceiveOauth2Exception(String msg) {
        super(msg);
    }

    @Override
    public int getHttpErrorCode() {
        return httpErrorCode;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return oAuth2ErrorCode;
    }

}
