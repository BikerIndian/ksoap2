package net.svishch.asoap.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Http2Reader;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


class LoggingInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();

        Buffer buffer = new Buffer();
        request.body().writeTo(buffer);
        String strRequest = buffer.readUtf8();


        String log1 = String.format("%n%n" +
                        "  ********** Client >> Server ********** %n" +
                        "  URL: %s on %s%n%n" +
                        "  ***** HEADERS  Client >> Server ***** %n" +
                        "%s%n" +
                        "  ***** BODY Client >> Server ***** %n" +
                        "%s",
                request.url(), chain.connection(), request.headers(), strRequest
        );

        Response response = chain.proceed(request);

        String responseBody = new String(response.body().bytes(), StandardCharsets.UTF_8);

        long t2 = System.nanoTime();
        String log2 = String.format("%n%n" +
                        " ********** Client << Server **********%n" +
                        "  URL: %s%n" +
                        "  RESPONSE TIME: %.1fms%n%n" +
                        "  ***** HEADERS Client << Server ***** %n" +
                        "%s%n" +
                        "  ***** BODY Client << Server ***** %n" +
                        "%s",
                response.request().url(), (t2 - t1) / 1e6, response.headers(), responseBody
        );

        Http2Reader.Companion.getLogger().info(log1);
        Http2Reader.Companion.getLogger().info(log2);

        ResponseBody body = ResponseBody.create(responseBody, response.body().contentType());
        return response.newBuilder().body(body).build();

    }
}