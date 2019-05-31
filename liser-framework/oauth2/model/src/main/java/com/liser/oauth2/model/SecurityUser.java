package com.liser.oauth2.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author LISER
 * @date 2019/5/25
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class SecurityUser implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 权限集
     */
    private List<Integer> authorities;
}
