package net.svishch.asoap.client;

import net.svishch.asoap.HttpResponseException;
import okhttp3.*;
import org.ksoap2.SoapEnvelope;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OkHttpClient {
    private static final String DEFAULT_CHARSET = "UTF-8";
    public static final int DEFAULT_TIMEOUT = 20000;
    protected static final String USER_AGENT_PREFIX = "ksoap2-okhttp/3.6.3";
    private ClientSettings clientSettings;
    private final String userAgent;
    private final okhttp3.OkHttpClient client;
    private final HttpUrl url;
    private final Headers headers;
    private Logger logger;
    private boolean debug = false;

    protected OkHttpClient(ClientSettings clientSettings)
    {

        this.clientSettings = clientSettings;
        this.debug = clientSettings.debug;


        okhttp3.OkHttpClient.Builder clientBuilder;
        if (null != clientSettings.client) {
            clientBuilder = clientSettings.client.newBuilder();
        } else {
            clientBuilder = new okhttp3.OkHttpClient.Builder();
        }

        if (clientSettings.debug) {
            clientBuilder.addInterceptor(new LoggingInterceptor());
            setDebug();
            this.logger = Logger.getLogger(this.getClass().getName());
            this.logger.setLevel(clientSettings.debug ? Level.FINEST : Level.INFO);
        }

        clientBuilder.connectTimeout((long) clientSettings.timeout, TimeUnit.MILLISECONDS).readTimeout((long) clientSettings.timeout, TimeUnit.MILLISECONDS);
        if (null != clientSettings.proxy) {
            clientBuilder.proxy(clientSettings.proxy);
            if (null != clientSettings.proxyAuthenticator) {
                clientBuilder.proxyAuthenticator(clientSettings.proxyAuthenticator);
            }
        }

        if (null != clientSettings.sslSocketFactory) {
            if (null == clientSettings.trustManager) {
                throw new NullPointerException("TrustManager = null");
            }

            clientBuilder.sslSocketFactory(clientSettings.sslSocketFactory, clientSettings.trustManager);
        }

        if (null != clientSettings.authenticator) {
            clientBuilder.authenticator(clientSettings.authenticator);
        }

        this.client = clientBuilder.build();
        this.userAgent = this.buildUserAgent(clientSettings);
        this.url = clientSettings.url;
        this.headers = clientSettings.headers;
    }

    private String buildUserAgent(ClientSettings clientSettings) {
        if (null != clientSettings.userAgent) {
            return clientSettings.userAgent;
        } else {
            String agent = System.getProperty("http.agent");
            if (null != agent) {
                Matcher m = Pattern.compile("(\\s\\(.*\\))").matcher(agent);
                if (m.find() && m.groupCount() > 0 && m.group(1).length() > 0) {
                    return "ksoap2-okhttp/3.6.3" + m.group(1);
                }
            }

            return "ksoap2-okhttp/3.6.3";
        }
    }

    public Headers call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {
        return this.call(soapAction, envelope, (Headers)null);
    }

    public Headers call(String soapAction, SoapEnvelope envelope, Headers headers) throws IOException, XmlPullParserException {

        if (soapAction == null) {
            soapAction = "\"\"";
        }

        String contentType = "text/xml;charset=utf-8";

        if (envelope.version == 120) {
            contentType = "application/soap+xml;charset=utf-8";
        }

        MediaType mediaType = MediaType.parse(contentType);

        byte[] requestData = envelope.getRequestData();
        sendLoggerFinest("Request Payload: " + new String(requestData, "UTF-8"));


        RequestBody body = RequestBody.create(requestData,mediaType);
        okhttp3.Request.Builder builder = (
                new okhttp3.Request.Builder())
                .url(this.url)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .post(body);


        /* BASIC */
        if (ClientSettings.AUTH_BASIC == this.clientSettings.authType) {
            String authorization = Credentials.basic(this.clientSettings.user, this.clientSettings.password);
            builder.addHeader("Authorization",authorization);
        }

        builder.addHeader("User-Agent", this.userAgent);
        builder.addHeader("ContentType",mediaType.toString());

        if (envelope.version != 120) {
            builder.addHeader("SOAPAction", soapAction);
        }

        sendLogger("SoapAction: " + soapAction);
        sendLogger("ContentType: " + contentType);

        int i;
        if (null != this.headers) {
            for(i = 0; i < this.headers.size(); ++i) {
                builder.addHeader(this.headers.name(i), this.headers.value(i));
            }
        }

        if (null != headers) {
            for(i = 0; i < headers.size(); ++i) {
                builder.addHeader(headers.name(i), headers.value(i));
            }
        }

        Request request = builder.build();
        sendLogger("Request Headers: " + request.headers().toString());
        Response response = this.client.newCall(request).execute();
        ResponseBody responseBody = null;

        Headers var12;
        try {
            if (response == null) {
                throw new HttpResponseException("Null response.", -1);
            }

            responseBody = response.body();
            if (responseBody == null) {
                throw new HttpResponseException("Null response body.", response.code());
            }

            Headers responseHeaders = response.headers();
            sendLogger("Response Headers: " + responseHeaders.toString());
            sendLoggerFinest("Response Payload (max first 32KB): " + response.peekBody(32768L).string());

            if (!response.isSuccessful()) {
                throw new HttpResponseException("HTTP request failed, HTTP status: " + response.code(), response.code(), responseHeaders);
            }

            envelope.parse(responseBody.byteStream());
            var12 = responseHeaders;
        } catch (HttpResponseException var18) {
            if (null != responseBody) {
                try {
                    envelope.parse(responseBody.byteStream());
                } catch (XmlPullParserException var17) {
                }
            }

            throw var18;
        } finally {
            if (null != responseBody) {
                responseBody.close();
            }

        }

        return var12;
    }



    private void sendLogger(String mess) {
        if (this.debug) {
            this.logger.fine(mess);
        }
    }

    private void sendLoggerFinest(String mess) {
        if (this.debug && this.logger.getLevel().intValue() <= Level.FINEST.intValue()) {
            this.logger.finest(mess);
        }
    }


    private void setDebug() {

        InputStream stream = null;

        try {
            stream = OkHttpClient.class.getResourceAsStream("logging.properties");
            if (null == stream) {
                stream = OkHttpClient.class.getClassLoader().getResourceAsStream("logging.properties");
            }

            if (null != stream) {
                LogManager.getLogManager().readConfiguration(stream);
            } else {
                System.err.println("Couldn't find logger configuration.");
            }
        } catch (IOException var10) {
            System.err.println("Couldn't read logger configuration.");
            var10.printStackTrace();
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException var9) {
                }
            }

        }

    }
}
