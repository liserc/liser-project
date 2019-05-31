package com.liser.oauth2.web.model;

/**
 * OAuth2错误状态枚举类
 *
 * @author LISER
 * @date 2019/4/15
 */
public enum OAuth2ErrorCode {

    /**
     * OAuth2Exception自定义返回信息
     */
    invalid_token(4010, "访问令牌无效或已过期"),

    invalid_client(4011, "无效的客户端"),

    insufficient_scope(4030, "客户权限范围不足"),

    invalid_grant(4000, "无效的授权类型"),

    invalid_request(4001, "无效的请求"),

    invalid_scope(4002, "无效的客户权限范围"),

    unauthorized_client(4012, "未经授权的客户"),

    unauthorized_user(4013, "未经授权的用户"),

    unsupported_grant_type(4003, "不支持的授予类型"),

    unsupported_response_type(4004, "不支持的响应类型"),

    access_denied(4005, "无访问权限");

    private int code;

    private String message;

    OAuth2ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
