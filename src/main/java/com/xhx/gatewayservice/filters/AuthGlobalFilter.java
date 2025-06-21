package com.xhx.gatewayservice.filters;


import com.xhx.gatewayservice.config.AuthProperties;
import com.xhx.gatewayservice.until.JwtUtils;

import entity.pojo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.xhx.gatewayservice.constant.Constant.*;

/**
 * @author master
 */
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final JwtUtils jwtUtils;
    private final AntPathMatcher autPathMather = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isExclude(path)){
            System.out.println(PATH_EXCLUDE + path);
            return chain.filter(exchange);
        }

        System.out.println(PATH_INCLUDE + path);

        String token = null;
        List<String> headers = request.getHeaders().get(AUTHORIZATION);
        if (headers != null && !headers.isEmpty()){
            token = headers.get(0);
        }

        if (token == null) {
            System.out.println(NULL_TOKEN);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        UserInfo userInfo;
        try {
            userInfo = jwtUtils.parseToken(token);
        } catch (Exception e) {
            System.out.println(TOKEN_ERROR + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String userId = userInfo.userId().toString();
        String role = userInfo.role();
        String ip = userInfo.ip();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder
                        .header(USER_INFO, userId)
                        .header(USER_ROLE, role)
                        .header(USER_IP, ip))
                .build();


        return chain.filter(swe);

    }

    private boolean isExclude(String path) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (autPathMather.match(pathPattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
