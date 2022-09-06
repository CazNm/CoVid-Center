package com.example.onboardingtestapplication.Model

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

private const val baseURL : String = "https://api.odcloud.kr/api/15077586/v1/"
private const val authKey : String = "bNmSjmL3NWL%2FmAmsQV0SyDT%2B8DCdZckhVg5%2FtSsmJHa47eBZBE%2BaFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg%3D%3D"
private const val serviceKey : String = "bNmSjmL3NWL/mAmsQV0SyDT+8DCdZckhVg5/tSsmJHa47eBZBE+aFvCHYxeM1Dsz2FcgQ64elqYL3mr6GUyjOg=="

object RetrofitObject {

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(provideOkHttpClient(AppInterceptor()))
        .addConverterFactory(GsonConverterFactory.create()) // 이건 어떤 기능을 하는지
        .build() // 최종적으로 뭘 만드는지 라이브러리 분석


    private fun provideOkHttpClient(interceptor : AppInterceptor) :OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class  AppInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {

          val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", authKey)
                .header("accept","application/json")
                .method(original.method, original.body)
                .build()

            return chain.proceed(request)
        }
    }

    private val coVidCenterAPI = retrofit.create(CoVidCenterInterface::class.java)
    val coVidCenterApi get() = coVidCenterAPI

}


interface CoVidCenterInterface {
    @GET("centers")
    fun getCenterList(
        @Query("page") pageIndex: Int,
        @Query("perPage") pageSize: Int,
        @Query("returnType") returnType : String = "JSON",
        @Query("serviceKey") serviceAuth : String = serviceKey
    ): Call<CovidCenterResponse>
}

