package com.liser.gateway;

import com.alibaba.fastjson.JSONArray;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author LISER
 * @date 2019/5/4
 */
@Slf4j
public class LiserGatewayAppTests {

    @Test
    public void testJwt() throws JOSEException, ParseException {

        RSAKey rsaJWK = new RSAKeyGenerator(1024)
                .keyID("123")
                .generate();

        RSAKey rsaPublicJWK = rsaJWK.toPublicJWK();

        JWSSigner signer = new RSASSASigner(rsaJWK);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("alice")
                .issuer("https://c2id.com")
                .expirationTime(new Date(new Date().getTime() + 60 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                claimsSet);

        signedJWT.sign(signer);

        String s = signedJWT.serialize();

        signedJWT = SignedJWT.parse(s);

        JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
        assertTrue(signedJWT.verify(verifier));

        assertEquals("alice", signedJWT.getJWTClaimsSet().getSubject());
        assertEquals("https://c2id.com", signedJWT.getJWTClaimsSet().getIssuer());
        assertTrue(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
    }

    @Test
    public void testJson() {
        // 角色一
        List<Integer> longs = Arrays.asList(1, 4, 3, 4, 5);

        // 角色2
        List<Integer> long2 = Arrays.asList(1, 125, 3, 4, 5);

        String string = JSONArray.toJSONString(longs);
        List<Integer> list = JSONArray.parseArray(string, Integer.class);
        log.info(string);
        log.info(list.toString());

        int position = 12;
        int number = 2;

        int i = list.get(position) & number;
        log.info("结果：{}", i);

    }
}
