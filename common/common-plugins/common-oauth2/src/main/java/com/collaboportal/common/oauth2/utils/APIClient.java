package com.collaboportal.common.oauth2.utils;

import java.util.concurrent.TimeUnit;

import com.collaboportal.common.oauth2.processor.APIClientProcessor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    /** サービス */
    APIClientProcessor apiClientProcessor;

    /**
     * セットアップ
     * 
     * @param baseUrl ベースURL
     */
    public APIClient(String baseUrl) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 接続タイムアウト
                .readTimeout(30, TimeUnit.SECONDS) // 読み込みタイムアウト
                .writeTimeout(30, TimeUnit.SECONDS) // 書き込みタイムアウト
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiClientProcessor = retrofit.create(APIClientProcessor.class);
    }

    /**
     * サービス取得
     * 
     * @return サービス
     */
    public APIClientProcessor getClient() {
        return apiClientProcessor;
    }
}
