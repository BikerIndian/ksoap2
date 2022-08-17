package net.svishch.asoap.client;


import okhttp3.Authenticator;
import okhttp3.Headers;
import okhttp3.HttpUrl;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.net.Proxy;

public class ClientSettings {

    public static final int AUTH_BASIC = 1;
   // public static final int AUTH_BEARER = 2;
   // public static final int AUTH_DIGEST = 3;
   // public static final int AUTH_HOBA = 4;
   // public static final int AUTH_MUTUAL = 5;
   // public static final int AUTH_AWS4_HMAC_SHA256 = 6;


    protected HttpUrl url;
    protected Proxy proxy = null;
    protected int timeout = 20000;
    protected String userAgent = null;
    protected Headers headers = null;
    protected okhttp3.OkHttpClient client = null;
    protected SSLSocketFactory sslSocketFactory = null;
    protected X509TrustManager trustManager = null;
    protected Authenticator authenticator = null;
    protected Authenticator proxyAuthenticator = null;
    protected boolean debug = false;
    protected String user = "";
    protected int authType = 0;
    protected String password = "";

    public ClientSettings(){

    }

    public ClientSettings(HttpUrl url) {
        this.url = url;
    }

    public ClientSettings(String url) {
        this.url = HttpUrl.parse(url);
    }

    public ClientSettings client(okhttp3.OkHttpClient client) {
        this.client = client;
        return this;
    }

    public ClientSettings proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public ClientSettings timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ClientSettings userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public ClientSettings headers(Headers headers) {
        this.headers = headers;
        return this;
    }

    public ClientSettings sslSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
        this.sslSocketFactory = sslSocketFactory;
        this.trustManager = trustManager;
        return this;
    }

    public ClientSettings authenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }

    public ClientSettings proxyAuthenticator(Authenticator authenticator) {
        this.proxyAuthenticator = authenticator;
        return this;
    }

    public ClientSettings debug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public OkHttpClient build() {
        return new OkHttpClient(this);
    }

    public ClientSettings setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public ClientSettings setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ClientSettings setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public ClientSettings setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    public ClientSettings setClient(okhttp3.OkHttpClient client) {
        this.client = client;
        return this;
    }

    public ClientSettings setSslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    public ClientSettings setTrustManager(X509TrustManager trustManager) {
        this.trustManager = trustManager;
        return this;
    }

    public ClientSettings setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
        return this;
    }

    public ClientSettings setProxyAuthenticator(Authenticator proxyAuthenticator) {
        this.proxyAuthenticator = proxyAuthenticator;
        return this;
    }

    public ClientSettings setAuthType(int authType) {
        this.authType = authType;
        return this;
    }

    public ClientSettings setAuthTypeNo() {
        this.authType = 0;
        return this;
    }

    public ClientSettings setAuthTypeBasic() {
        this.authType = AUTH_BASIC;
        return this;
    }

    public ClientSettings setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public ClientSettings setUser(String user) {
        this.user = user;
        return this;
    }

    public ClientSettings setPassword(String password) {
        this.password = password;
        return this;
    }

    public ClientSettings setUrl(String urlIn) {
        this.url = HttpUrl.parse(urlIn);
        return this;
    }
}
