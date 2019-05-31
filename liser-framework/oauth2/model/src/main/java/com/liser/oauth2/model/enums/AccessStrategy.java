package com.liser.oauth2.model.enums;

/**
 * @author LISER
 * @date 2019/5/8
 */
public enum AccessStrategy {

    /**
     * 开发访问
     */
    open(1),

    /**
     * 已认证
     */
    authenticated(2),

    /**
     * 权限校验
     */
    verification(3);

    private Integer code;

    AccessStrategy(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
