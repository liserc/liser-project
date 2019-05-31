package com.liser.oauth2.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author LISER
 */
@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2User implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 帐户未过期
     */
    private boolean accountNonExpired;

    /**
     * 帐户未锁定
     */
    private boolean accountNonLocked;

    /**
     * 凭据未过期
     */
    private boolean credentialsNonExpired;

    /**
     * 启用
     */
    private boolean enabled;

    /**
     * 权限
     */
    private List<String> authorities;

}
