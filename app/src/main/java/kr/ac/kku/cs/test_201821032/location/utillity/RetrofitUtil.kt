package kr.ac.kku.cs.test_201821032.location.utillity

import kr.ac.kku.cs.test_201821032.BuildConfig
import kr.ac.kku.cs.test_201821032.location.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitUtil {
    val apiService: ApiService by lazy { getRetrofit().create(ApiService::class.java) }

    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(Url.TMAP_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildOkHttpClient())
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        // 매번 api 를 호출할 때마다 HttpLoggingInterceptor를 통해서 그 로그를 찍어줄 수 있도록
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)     // 5초동안 api에 대한 응답이 없으면 에러 발생
            .addInterceptor(interceptor)
            .build()
    }
}