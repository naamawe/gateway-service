package com.xhx.gatewayservice.constant;

/**
 * @author master
 */
public class Constant {
    public static final String PATH_EXCLUDE = "路径被排除，直接放行: ";
    public static final String PATH_INCLUDE = "路径未被排除，需要认证: ";
    public static final String AUTHORIZATION = "Authorization";
    public static final String NULL_TOKEN = "Token为空，返回401";
    public static final String TOKEN_ERROR = "Token解析失败: ";
    public static final String USER_INFO = "user-Info";
    public static final String USER_ROLE = "user-Role";
    public static final String USER_IP = "user-Ip";

}
