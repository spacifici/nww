package com.nww.munich.newssharer.sharing;

import com.nww.munich.newssharer.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.net.URLEncoder;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class ApiRequests {

    public static final ApiRequests sharedInstace = new ApiRequests();


    private final OkHttpClient okHttpClient;

    private ApiRequests() {
        okHttpClient = new OkHttpClient();
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
}
