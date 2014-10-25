package com.nww.munich.newssharer;

/**
 * Created by Stefano Pacifici on 25/10/14.
 */
public class Constants {

    @SuppressWarnings("UnusedDeclaration")
    public static final String DEVEL_BASE_URL = "http://192.168.3.164:5050";

    @SuppressWarnings("UnusedDeclaration")
    public static final String STAGING_BASE_URL = "https://sleepy-mountain-8434.herokuapp.com";

    public static final String BASE_URL = DEVEL_BASE_URL;

    public static final String CREATE_ARTICLE_URL = BASE_URL + "/api/article?url=%s";
}
