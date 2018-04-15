package com.leapord.supercoin.network;

import android.text.TextUtils;
import android.util.Log;

import com.leapord.supercoin.app.CoinApplication;
import com.leapord.supercoin.app.OkCoin;
import com.leapord.supercoin.util.MD5Util;
import com.leapord.supercoin.util.SslUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: Administrator
 * @description:
 * @date: 2017-08-04   09:21
 */
public class HttpUtil {

    private static final String BASE_URL = "https://www.okex.cn/";

    //设置缓存目录
    private static final File cacheDirectory =
            new File(CoinApplication.INSTANCE.getCacheDir().getAbsolutePath(), "SuperCoinCache");
    private static Cache cache = new Cache(cacheDirectory, 10 * 1024 * 1024);

    //请求拦截
    private static Interceptor requestInterceptor = chain -> {
        Request original = chain.request();
        //请求定制：添加请求头
        Request.Builder requestBuilder = original
                .newBuilder()
                ///由于所有网络请求都是post json的方式,因此此处添加了公共头
                ///用户可以根据自己的业务自行修改
                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String method = chain.request().method();
        if (TextUtils.equals("POST", method)) {
            FormBody formBody = null;
            if (original.body() instanceof FormBody) {
                formBody = (FormBody) original.body();
            }
            Map<String, String> params = new HashMap<>();
            FormBody.Builder newFormBody = new FormBody.Builder();
            if (original.url().toString().startsWith(BASE_URL)) {
                newFormBody.addEncoded("api_key", OkCoin.API.API_KEY);
                params.put("api_key", OkCoin.API.API_KEY);
            }
            if (formBody != null) {
                int size = formBody.size();
                for (int i = 0; i < size; i++) {
                    String key = formBody.encodedName(i);
                    String value = formBody.encodedValue(i);
                    params.put(key, value);
                    newFormBody.addEncoded(key, value);
                }
            }
            String mysignV1 = MD5Util.buildMysignV1(params, OkCoin.API.SECRET_KEY);
            newFormBody.addEncoded("sign", mysignV1);
            requestBuilder.method(original.method(), newFormBody.build());

        }

        return chain.proceed(requestBuilder.build());
    };

    //响应拦截器
    private static Interceptor responseInterceptor = new Interceptor() {
        String emptyString = ":\"\"";
        String emptyObject = ":{}";
        String emptyArray = ":[]";
        String newChars = ":null";

        @Override
        public Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String json = responseBody.string();
                MediaType contentType = responseBody.contentType();
                if (!json.contains(emptyString)) {
                    ResponseBody body = ResponseBody.create(contentType, json);
                    return response.newBuilder().body(body).build();
                } else {
                    String replace = json.replace(emptyString, newChars);
                    String replace1 = replace.replace(emptyObject, newChars);
                    String replace2 = replace1.replace(emptyArray, newChars);
                    ResponseBody body = ResponseBody.create(contentType, replace2);
                    return response.newBuilder().body(body).build();
                }
            }
            return response;
        }
    };

    private static OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .cookieJar(CookieJar.NO_COOKIES)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .hostnameVerifier(((hostname, session) -> true))
            .followRedirects(true)
            .sslSocketFactory(SslUtils.initSSLSocketFactory(), SslUtils.initTrustManager())
            .addInterceptor(requestInterceptor)
            .addInterceptor(responseInterceptor)
            .addInterceptor(new HttpLoggingInterceptor(message -> Log.i("SuperCoin", message)).setLevel(HttpLoggingInterceptor.Level.BODY))
            .cache(cache)
            .build();


    private static OkCoinService retrofitService = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            // 添加Gson转换器
            .addConverterFactory(GsonConverterFactory.create())
            // 添加Retrofit到RxJava的转换器
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(mOkHttpClient)
            .build()
            .create(OkCoinService.class);

    public static OkCoinService createRequest() {
        return retrofitService;
    }


}
