package com.accenture.dansmarue.services;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Interceptor managing the weird behavior of SIRA Service that can send back JsonArray OR JsonObject
 * Created by PK on 10/04/2017.
 */
public class SiraHttpResponseInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.isSuccessful()) {
            final ResponseBody responseBody = response.body();
            final MediaType contentType = responseBody.contentType();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            //Rewrite of the responsebody
            final String actualBody = buffer.clone().readString(UTF8);
            if (actualBody.startsWith("[") && actualBody.endsWith("]")) {
                final String convertedBody = actualBody.subSequence(1, actualBody.length() - 1).toString();
                ResponseBody body = ResponseBody.create(contentType, convertedBody);
                return response.newBuilder().body(body).build();
            }
        }

        return response;
    }
}
