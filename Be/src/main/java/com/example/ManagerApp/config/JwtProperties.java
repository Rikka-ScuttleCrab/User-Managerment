package com.example.ManagerApp.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix = "managerapplication.jwt")
public class JwtProperties {
    private String base64Secret;
    private long accessTokenValidityInSeconds;
    private long refreshTokenValidityInSeconds;
    // getters & setters
    public String getBase64Secret() {
        return base64Secret;
    }
    public void setBase64Secret(String base64Secret) {
        this.base64Secret = base64Secret;
    }
    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidityInSeconds;
    }
    public void setAccessTokenValidityInSeconds(long accessTokenValidityInSeconds) {
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
    }
    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInSeconds;
    }
    public void setRefreshTokenValidityInSeconds(long refreshTokenValidityInSeconds) {
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
    }
}