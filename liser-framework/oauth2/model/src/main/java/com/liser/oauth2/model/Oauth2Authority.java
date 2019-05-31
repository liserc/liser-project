package com.liser.oauth2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LISER
 * @date 2019/3/27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2Authority implements Serializable {

    /**
     * 资源路径
     */
    private String url;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 访问策略（1开放访问，2已认证，3校验权限）
     */
    private Integer accessStrategy;

    /**
     * 权限值
     */
    private Long value;

    /**
     * 权限层级
     */
    private Integer position;
}
