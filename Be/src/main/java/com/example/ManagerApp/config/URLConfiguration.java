package com.example.ManagerApp.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Component
@ConfigurationProperties(prefix = "managerapplication")
public class URLConfiguration{
    private String baseUrl;
    public String getBaseURL() {
        return baseUrl;
    }
    public void setBaseURL(String baseURL) {
        this.baseUrl = baseURL;
    }
}