package com.example.ManagerApp.service.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    @JsonProperty(value = "status_code")
    private int statusCode;

    private String error;

    private Object message;

    private T data;
}
