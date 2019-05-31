package com.liser.oauth2.token.store;

import com.liser.oauth2.model.Oauth2Authority;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author LISER
 * @date 2019/4/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthenticationWrapper implements Serializable {

    /**
     * 客户端ID
     */
    private String clientId;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 用户名
     */
    private String username;

    /**
     * 权限集合
     */
    private List<Oauth2Authority> authorities;

}
