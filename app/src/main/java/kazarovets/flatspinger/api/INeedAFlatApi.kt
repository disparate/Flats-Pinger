package kazarovets.flatspinger.api

import android.text.format.DateUtils
import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.INeedAFlatListResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class INeedAFlatApi {

    companion object {
        val TAG = "INeedAFlatApi"
        private val BASE_URL = "http://ineedaflat.by/ineedaflat-server/"
    }

    val iNeedAFlatApiService by lazy { createApiService() }

    private fun createApiService(): INeedAFlatApiService {

        val httpInterceptor = HttpLoggingInterceptor()
        httpInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val okHttpBuilder = OkHttpClient.Builder()
                .addInterceptor(httpInterceptor)
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
//                .addInterceptor { chain ->
//                    val original = chain.request()
//
//                    //adding header info
//                    val request = original.newBuilder()
//                            .header("Content-Type", "application/json")
//                            .header("Accept", "application/json")
//                            .method(original.method(), original.body())
//                            .build()
//
//                    return@addInterceptor chain.proceed(request)
//                }

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        return retrofit.create(INeedAFlatApiService::class.java)
    }

    fun getFlats(minCost: Double?, maxCost: Double?, agencyAllowed: Boolean): Single<List<Flat>> {
        val min = if (minCost != null) minCost else 0.0
        val max = if (maxCost != null) maxCost else 100000.0
        var query = "{\"attributes.price.value\":{\"\$gte\":$min,\"\$lte\":$max}," +
                (if (agencyAllowed) "" else "\"agentImpudence\":{\"\$lte\":15.0},") +
//                "\"attributes.geoCoordinates\":{\"\$geoWithin\":{\"\$polygon\":[" +
//                "[27.551849484443665,53.90512705553124]," +
//                "[27.54768267273903,53.90359566086626]," +
//                "[27.54117462784052,53.90586340073318]," +
//                "[27.5334582477808,53.90566766216741]," +
//                "[27.522237226366997,53.91170294066077]," +
//                "[27.512641958892345,53.91706696638249]," +
//                "[27.505323886871338,53.92372310321118]," +
//                "[27.496830001473427,53.927506505003834]," +
//                "[27.473903819918633,53.929468353231016]," +
//                "[27.45512031018734,53.9345906757086]," +
//                "[27.441215738654137,53.938946622476166]," +
//                "[27.43372030556202,53.941783565722695]," +
//                "[27.446649223566055,53.951259037767294]," +
//                "[27.465266436338425,53.94269430694159]," +
//                "[27.474488206207752,53.94255162875316]," +
//                "[27.49278087168932,53.9386756472995]," +
//                "[27.51359112560749,53.93487549391604]," +
//                "[27.522090040147308,53.929109668408316]," +
//                "[27.52436690032482,53.925279425063465]," +
//                "[27.534565664827824,53.91715641692136]]}}," +
                "\"attributes.rooms\":{\"\$in\":[2.0]}}"
        query = query.replace("{", "%7B").replace("}", "%7D")
                .replace("[", "%5B").replace("]", "%5D")
//        val createdAt = System.currentTimeMillis() - daysUpdatedAgo * DateUtils.DAY_IN_MILLIS
        return iNeedAFlatApiService.getFlats(query)
                .map {
                    Log.d(TAG, "started parsing flats")
                    ApiManager.GSON.fromJson(it, INeedAFlatListResponse::class.java)
                }
                .map {
                    Log.d(TAG, "finished parsing flats")
                    it.items
                }
    }

    private fun getRoomsParam() {

    }
}