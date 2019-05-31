package com.liser.oauth2.model;

/**
 * @author LISER
 * @date 2019/3/28
 */
public class Oauth2Constant {

    /**
     * 网关无需令牌访问自定义添加的请求头
     */
    public static final String X_GATEWAY_TOKEN = "x-gateway-token";

    /**
     * 通用唯一标示符
     */
    public static final String USER_ID = "user_id";

    /**
     * 密码模式登陆模式
     */
    public static final String PASSWORD = "password";

    /**
     * 手机号模式登陆模式
     */
    public static final String MOBILE = "mobile";

    /**
     * openid登陆模式
     */
    public static final String OPENID = "openid";

    /**
     * 客户端模式
     */
    public static final String CLIENT = "client_credentials";

    /**
     * 主体
     */
    public static final String SUBJECT = "sub";

    /**
     * 权限集合
     */
    public static final String AUTHORITIES = "authorities";
}
