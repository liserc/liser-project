package com.liser.oauth2.token.store;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author LISER
 * @date 2019/4/21
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AccessTokenWrapper implements Serializable {

    /**
     * 刷新令牌ati
     */
    private String jti;

}
