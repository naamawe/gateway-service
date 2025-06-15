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
        System.out.println("=== 调试信息开始 ===");
        System.out.println("进入全局过滤器：" + exchange.getRequest().getPath().value());
        System.out.println("AuthProperties: " + authProperties);
        System.out.println("排除路径配置：" + authProperties.getExcludePaths());
        System.out.println("=== 调试信息结束 ===");

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isExclude(path)){
            System.out.println("✓ 路径被排除，直接放行: " + path);
            return chain.filter(exchange);
        }

        System.out.println("✗ 路径未被排除，需要认证: " + path);

        String token = null;
        List<String> headers = request.getHeaders().get("Authorization");
        if (headers != null && !headers.isEmpty()){
            token = headers.get(0);
        }

        if (token == null) {
            System.out.println("Token为空，返回401");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        UserInfo userInfo;
        try {
            userInfo = jwtUtils.parseToken(token);
        } catch (Exception e) {
            System.out.println("Token解析失败: " + e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String userId = userInfo.userId().toString();
        String role = userInfo.role();
        String ip = userInfo.ip();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder
                        .header("user-Info", userId)
                        .header("user-Role", role)
                        .header("user-Ip", ip))
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
