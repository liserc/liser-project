package com.liser.oauth2.token.store;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author LISER
 * @date 2019/5/6
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthToAccessWrapper {

    /**
     * 刷新令牌ati
     */
    private String jti;

}
