package com.liser.oauth2.authentication.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author LISER
 * @date 2019/5/11
 */
public class Oauth2Authentication extends AbstractAuthenticationToken {

    /**
     * 标识
     */
    private String identifier;

    /**
     * 凭证
     */
    private String credential;

    public Oauth2Authentication(String identifier, String credential) {
        super(null);
        this.identifier = identifier;
        this.credential = credential;
        this.setAuthenticated(false);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public Oauth2Authentication(String identifier, String credential, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.identifier = identifier;
        this.credential = credential;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credential;
    }

    @Override
    public Object getPrincipal() {
        return this.identifier;
    }
}
