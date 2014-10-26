package com.nww.munich.newssharer.sharing;

import com.nww.munich.newssharer.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class ApiRequests {

    public static final ApiRequests sharedInstace = new ApiRequests();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    private final OkHttpClient okHttpClient;

    private ApiRequests() {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(3, TimeUnit.SECONDS);
    }

    public void articleRequest(String url, Callback callback) {
        final String requestUrl = String.format(
                Constants.CREATE_ARTICLE_URL,
                URLEncoder.encode(url)
        );

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(requestUrl).get().build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public void articlePost(String id, JSONObject article, Callback callback) {
        Request.Builder builder = new Request.Builder();
        RequestBody body = RequestBody.create(JSON, article.toString());
        Request request = builder
                .url(String.format(Constants.POST_ARTICLE_URL, id))
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
