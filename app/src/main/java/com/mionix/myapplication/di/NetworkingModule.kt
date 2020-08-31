package com.mionix.myapplication.di


//
//class AuthInterceptor : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//
////        Headers headers = new Headers.Builder()
////            .add("Authorization", "auth-value")
////            .add("User-Agent", "you-app-name")
////            .build();
//
//        val originalRequest = chain.request()
//        val url = chain.request().url.newBuilder()
//            .addQueryParameter("api_key", "4e2895c2da6e111db3b75e57882e7ff5")
//            .build()
//        val newRequest = originalRequest.newBuilder()
//            .addHeader("content-type", "application/json") //thêm một header với name và value.
//            .addHeader("token", "123456789")
//            .url(url)
////            .cacheControl(CacheControl.FORCE_CACHE) //  Đặt kiểm soát header là của request này, replace lên mọi header đã có.
////            .headers(headers) //Removes all headers on this builder and adds headers.
////            .method(originalRequest.method) // Adds request method and request body
////            .removeHeader("Authorization") // Removes all the headers with this name
//            .build()
//
////        val newRequest = chain.request()
////            .newBuilder()
////            .url(newUrl)
////            .build()
//
//
//        return chain.proceed(newRequest)
//    }
//}
//
//
//val retrofitModule = module {
//    single { provideHttpClient() }
//    single { provideRetrofit(get()) }
//    single { provideApiService(get()) }
//}
//
//
//fun provideHttpClient(): OkHttpClient {
//    val logging = HttpLoggingInterceptor()
//    logging.level = if (BuildConfig.DEBUG)
//        HttpLoggingInterceptor.Level.BODY
//    else
//        HttpLoggingInterceptor.Level.NONE
//    return OkHttpClient().newBuilder()
//        .addInterceptor(logging)
//        .addInterceptor(AuthInterceptor())
//        .build()
//}
//
//fun provideRetrofit(client: OkHttpClient): Retrofit {
//    return Retrofit.Builder()
//        .baseUrl("https://api.themoviedb.org/3/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .client(client)
//        .build()
//}
//
//fun provideApiService(retrofit: Retrofit): Api {
//    return retrofit.create(Api::class.java)
//}