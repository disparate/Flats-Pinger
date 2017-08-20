package kazarovets.flatspinger.api

import android.content.Context
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.OnlinerFlatsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory




class OnlinerApi constructor(context: Context) {

    companion object {
        private val BASE_URL = "https://ak.api.onliner.by/";
    }

    val onlinerApiService by lazy { createService() }

    public fun getLatestFlats(priceMin: Int, priceMax: Int): Single<List<Flat>> {
        val rentType = "1_room"
        val currency = "USD"
        return onlinerApiService.getFlats(rentType, priceMin, priceMax, currency)
                .map {ApiManager.GSON.fromJson(it, OnlinerFlatsResponse::class.java).flatsList}
    }

    fun createService(): OnlinerApiService {


        val httpInterceptor = HttpLoggingInterceptor()
        httpInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val okHttpBuilder = OkHttpClient.Builder()
                .addInterceptor(httpInterceptor)
                .addInterceptor { chain ->
                    val original = chain.request()

                    //adding header info
                    val request = original.newBuilder()
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build()

                    return@addInterceptor chain.proceed(request)
                }

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        return retrofit.create(OnlinerApiService::class.java)

    }


}