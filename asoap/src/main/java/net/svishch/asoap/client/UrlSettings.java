package net.svishch.asoap.client;

import org.ksoap2.SoapEnvelope;

public class UrlSettings {

    public static final int AUTH_BASIC = 1;
    public static final int AUTH_BEARER = 2;
    public static final int AUTH_DIGEST = 3;
    public static final int AUTH_HOBA = 4;
    public static final int AUTH_MUTUAL = 5;
    public static final int AUTH_AWS4_HMAC_SHA256 = 6;


    String url = "";
    String user = "";
    String password = "";
    int soapVersion = SoapEnvelope.VER12;

    boolean debug = false;

    public UrlSettings setUrl(String url) {
        this.url = url;
        return this;
    }

    public UrlSettings setUser(String user) {
        this.user = user;
        return this;
    }

    public UrlSettings setPassword(String password) {
        this.password = password;
        return this;
    }

    public UrlSettings setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDebug() {
        return debug;
    }
}
