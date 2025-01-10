package com.library.book_storage_service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenReqRes {

    private String username;
    private String password;

    private String token;
    private String expirationTime;

    public TokenReqRes() {
    }

    public TokenReqRes(String username, String password, String token, String expirationTime) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.expirationTime = expirationTime;
    }

    @Override
    public String toString() {
        return "TokenReqRes{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", expirationTime='" + expirationTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TokenReqRes that = (TokenReqRes) o;
        return Objects.equals(username, that.username) && Objects.equals(password, that.password) && Objects.equals(token, that.token) && Objects.equals(expirationTime, that.expirationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, token, expirationTime);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }
}