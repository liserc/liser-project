package com.liser.oauth2.authorization;

import com.liser.oauth2.model.Oauth2Authority;

import java.util.List;

/**
 * @author LISER
 * @date 2019/4/16
 */
public interface ApiAuthoritiesStore {

    /**
     * 初始化权限集合
     */
    void initAuthorities();

    /**
     * 加载权限集合
     *
     * @return
     */
    List<Oauth2Authority> loadAuthorities();

    /**
     * 刷新权限集合
     */
    void refreshAuthorities();
}
