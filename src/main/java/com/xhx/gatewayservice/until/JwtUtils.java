package com.xhx.gatewayservice.until;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import entity.pojo.UserInfo;
import exception.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.security.KeyPair;

/**
 * @author master
 */
@Component
public class JwtUtils {
    private final JWTSigner jwtSigner;

    public JwtUtils(KeyPair keyPair) {
        this.jwtSigner = JWTSignerUtil.createSigner("rs256", keyPair);
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 解析刷新token得到的用户信息
     */
    public UserInfo parseToken(String token) {
        // 1.校验token是否为空
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }
        // 2.校验并解析jwt
        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            throw new UnauthorizedException("无效的token", e);
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            throw new UnauthorizedException("无效的token");
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("token已经过期");
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayload("user");
        Object rolePayload = jwt.getPayload("role");
        Object ipPayload = jwt.getPayload("ip");

        if (userPayload == null || rolePayload == null) {
            throw new UnauthorizedException("token缺失必要字段");
        }

        // 5.数据解析
        try {
            Long userId = Long.valueOf(userPayload.toString());
            String role = rolePayload.toString();
            String ip = ipPayload.toString();
            return new UserInfo(userId, role, ip);
        } catch (RuntimeException e) {
            // 数据格式有误
            throw new UnauthorizedException("无效的token");
        }
    }

}