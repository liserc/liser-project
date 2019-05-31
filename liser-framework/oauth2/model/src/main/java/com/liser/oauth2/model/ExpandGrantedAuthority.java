package com.liser.oauth2.model;

import java.io.Serializable;

/**
 * @author LISER
 * @date 2019/4/14
 */
public interface ExpandGrantedAuthority extends Serializable {

    /**
     * 设置权限
     *
     * @param url
     */
    void setAuthority(String url);

    /**
     * 获取请求方式
     *
     * @return
     */
    String getMethod();

    /**
     * 获取权限值
     *
     * @return
     */
    Long getValue();

    /**
     * 权限层级
     *
     * @return
     */
    Integer getPosition();


}
