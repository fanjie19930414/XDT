package com.kotlin.baselibrary.net

import com.kotlin.baselibrary.commen.BaseConstant
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @desciption: Retrofit工厂，单例
 */
class RetrofitFactory private constructor(){
    private val retrofit : Retrofit
    private val headInterceptor : Interceptor

    companion object {
        val retrofitFactoryInstance : RetrofitFactory by lazy { RetrofitFactory() }
    }

    init {
        // 添加请求头
        headInterceptor = Interceptor {
            chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Content-Type","application/json")
                .addHeader("charset","utf-8")
                .build()
            chain.proceed(request)
        }
        // 配置Retorfit
        retrofit = Retrofit.Builder()
            .baseUrl(BaseConstant.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(initClient())
            .build()
    }

    /**
     * okhttp创建
     */
    private fun initClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(initLogInterceptor())
            .addInterceptor(headInterceptor)
            .connectTimeout(10,TimeUnit.SECONDS)
            .readTimeout(10,TimeUnit.SECONDS)
            .build()
    }

    /**
     * 日志拦截
     */
    private fun initLogInterceptor(): Interceptor{
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    /**
     * 具体module实例化
     */
    fun <T> create(service: Class<T>) : T{
        return retrofit.create(service)
    }
}