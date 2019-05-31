package com.liser.gateway.filters;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求url权限过滤器
 *
 * @author LISER
 */
@Slf4j
public class GatewayAccessFilter implements GlobalFilter, Ordered {


    private final OAuth2RestTemplate oAuth2RestTemplate;

    private static final byte[] RESPONSE_MESSAGE = JSON.toJSONBytes("测试");

    public GatewayAccessFilter(OAuth2RestTemplate oAuth2RestTemplate) {
        this.oAuth2RestTemplate = oAuth2RestTemplate;
    }

    /**
     * 调用访问认证服务判断是否具有访问权限，无访问权限则返回401给客户端
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        log.info("访问：{}", request.getPath());



        // 判断是否具有访问权限
//        if (result.isAuthenticated()) {
//            // 判断是否是客户端访问模式
//            if (result.isClientMode()) {
//                ServerHttpRequest.Builder builder = request.mutate();
//                // 获取服务间访问令牌
//                OAuth2AccessToken accessToken = oAuth2RestTemplate.getAccessToken();
//                // 添加请求头
//                builder.header(HttpHeaders.AUTHORIZATION, String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, accessToken.getValue()));
//
//                return chain.filter(exchange.mutate().request(builder.build()).build());
//            }
//
//            return chain.filter(exchange);
//        }

        return unauthorized(exchange);
    }

    /**
     * 网关返回401无访问权限
     *
     * @param serverWebExchange
     * @return
     */
    private Mono<Void> unauthorized(ServerWebExchange serverWebExchange) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        // 添加响应头信息
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.CONTENT_LENGTH, "" + RESPONSE_MESSAGE.length);
        // 响应状态
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        // 响应体数据
        DataBuffer buffer = response.bufferFactory().wrap(RESPONSE_MESSAGE);

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
