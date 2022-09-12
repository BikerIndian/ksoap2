package net.svishch.asoap;

import okhttp3.Headers;

import java.io.IOException;

public class HttpResponseException extends IOException {
    private int statusCode;
    private Headers responseHeaders;
    private String body = "";

    HttpResponseException(int statusCode) {
        this((String)null, statusCode);
    }

    public HttpResponseException(String message, int statusCode) {
        this(message, statusCode, (Headers)null,"");
    }

    public HttpResponseException(String message, int statusCode, Headers responseHeaders, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseHeaders = responseHeaders;
        this.body = responseBody;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Headers getResponseHeaders() {
        return this.responseHeaders;
    }

    public String getBody() {
        return body;
    }
}
