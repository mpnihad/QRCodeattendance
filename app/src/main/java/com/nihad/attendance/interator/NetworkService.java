package com.nihad.attendance.interator;

import android.content.Context;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.nihad.attendance.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class NetworkService {

//    public static APIService service;
//    public static Retrofit retrofit;
//
//    public static APIService getService() {
//
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(BuildConfig.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();
//
//        service = retrofit.create(APIService.class);
//
//        return service;
//    }
        private static Retrofit retrofit = null;
        private static int REQUEST_TIMEOUT = 60;
        private static OkHttpClient okHttpClient;

        public static Retrofit getClient(Context context) {

//            if (okHttpClient == null)
//                initOkHttp(context);

            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BuildConfig.BASE_URL)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }

//        private static void initOkHttp(final Context context) {
//            OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
//                    .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
//                    .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
//                    .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
//
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//            httpClient.addInterceptor(interceptor);
//
//            httpClient.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request original = chain.request();
//                    Request.Builder requestBuilder = original.newBuilder()
//                            .addHeader("Accept", "application/json")
//                            .addHeader("Content-Type", "application/json");
//
//                    // Adding Authorization token (API Key)
//                    // Requests will be denied without API key
//                    if (!TextUtils.isEmpty(PrefUtils.getApiKey(context))) {
//                        requestBuilder.addHeader("Authorization", PrefUtils.getApiKey(context));
//                    }
//
//                    Request request = requestBuilder.build();
//                    return chain.proceed(request);
//                }
//            });
//
//            okHttpClient = httpClient.build();
//        }


    }

