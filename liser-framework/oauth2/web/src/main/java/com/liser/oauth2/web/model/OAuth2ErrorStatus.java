package com.liser.oauth2.web.model;

/**
 * 认证错误状态码
 *
 * @author LISER
 * @date 2019/4/15
 */
public enum OAuth2ErrorStatus {
    /**
     * AuthenticationException自定义返回信息
     */
    unknown_anomaly(40999, "未知异常"),

    access_token_required(4014, "访问令牌为空"),

    account_expired(4015, "帐户已过期"),

    account_status(4016, "帐户状态"),

    bad_credentials(4018, "坏的凭证"),

    captcha_error(4019, "验证码错误"),

    credentials_expired(40100, "凭证已过期"),

    disabled(40101, "用户已失效"),

    locked(40102, "用户已锁定"),

    username_not_found(40103, "用户不存在"),

    password_error(40104, "密码错误"),

    no_access(40105, "无访问权限");

    private int code;

    private String message;

    OAuth2ErrorStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
