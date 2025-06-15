package com.xhx.gatewayservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author master
 */
@ConfigurationProperties(prefix = "system.auth")
@Component
public class AuthProperties {
    private List<String> includePaths;
    private List<String> excludePaths;

    public AuthProperties() {
    }

    public AuthProperties(List<String> includePaths, List<String> excludePaths) {
        this.includePaths = includePaths;
        this.excludePaths = excludePaths;
    }

    /**
     * 获取
     * @return includePaths
     */
    public List<String> getIncludePaths() {
        return includePaths;
    }

    /**
     * 设置
     * @param includePaths
     */
    public void setIncludePaths(List<String> includePaths) {
        this.includePaths = includePaths;
    }

    /**
     * 获取
     * @return excludePaths
     */
    public List<String> getExcludePaths() {
        return excludePaths;
    }

    /**
     * 设置
     * @param excludePaths
     */
    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    @Override
    public String toString() {
        return "AuthProperties{includePaths = " + includePaths + ", excludePaths = " + excludePaths + "}";
    }
}
