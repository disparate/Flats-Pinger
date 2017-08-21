package kazarovets.flatspinger.api

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

    fun getFlats(minCost: Double, maxCost: Double): Single<List<Flat>> {
        var query = "{\"attributes.price.value\":{\"\$gte\":$minCost,\"\$lte\":$maxCost}," +
                "\"attributes.geoCoordinates\":{\"\$geoWithin\":{\"\$polygon\":[" +
                "[27.451969720423218,53.911758829765866]" +
                ",[27.443541884422306,53.90599553856022]" +
                ",[27.446632795035843,53.898671233240194]" +
                ",[27.461070120334625,53.89888715456463]" +
                ",[27.49767690896988,53.90248969551207]" +
                ",[27.515458278357986,53.90099830455015]" +
                ",[27.527507096529007,53.89970697376876]" +
                ",[27.529254555702206,53.89678933189394]" +
                ",[27.511713914573193,53.89043911677599]" +
                ",[27.49264843761921,53.881633242767776]" +
                ",[27.481959499418736,53.87080229248024]" +
                ",[27.47086152434349,53.86027047347173]" +
                ",[27.465063259005543,53.84758448532221]" +
                ",[27.469033263623714,53.842856612731524]" +
                ",[27.479105293750763,53.84322533557174]" +
                ",[27.486967854201794,53.85541094534421]" +
                ",[27.495760805904865,53.863386997677125]" +
                ",[27.502802610397342,53.873428013374514]" +
                ",[27.51445915549993,53.87878509547597]" +
                ",[27.530817613005638,53.88591547446635]" +
                ",[27.53755196928978,53.89060015123559]" +
                ",[27.5482901930809,53.889114066120214]" +
                ",[27.560532800853252,53.89196506625321]," +
                "[27.571267001330853,53.88563684335372]," +
                "[27.58509110659361,53.88137572969092]," +
                "[27.6014881208539,53.8872617701224]," +
                "[27.60772962123156,53.88427666211919]," +
                "[27.62293841689825,53.86832744461059]," +
                "[27.63462983071804,53.86683957917759]," +
                "[27.64362562447786,53.872814048686145]," +
                "[27.63610940426588,53.8828016167887]," +
                "[27.624134346842766,53.89316614669312]," +
                "[27.607945874333385,53.898618289872275]," +
                "[27.592159062623978,53.89690035970925]," +
                "[27.570674568414688,53.897421119688914]," +
                "[27.58123140782118,53.90262204154206]," +
                "[27.589066475629807,53.90845749202686]," +
                "[27.595607712864876,53.91428956015908]," +
                "[27.61460244655609,53.917822254645436]," +
                "[27.626640200614926,53.91966470576814]," +
                "[27.6380580291152,53.92438172880118]," +
                "[27.65161693096161,53.92827680496924]," +
                "[27.660352885723114,53.93172679263055]," +
                "[27.672658525407314,53.93569638319558]," +
                "[27.673646584153175,53.94043606640377]," +
                "[27.665759213268757,53.94309096158173]," +
                "[27.65771862119436,53.94028805196268]," +
                "[27.64224797487259,53.93634199070901]," +
                "[27.62596964836121,53.9325477438525]," +
                "[27.61475533246994,53.92841143820383]," +
                "[27.59933196008205,53.92218232098436]," +
                "[27.59834691882134,53.92129423281537]," +
                "[27.599598839879036,53.92304610582574]," +
                "[27.60249461978674,53.92407255513307]," +
                "[27.601364739239216,53.92529225761693]," +
                "[27.599359788000584,53.92579055248881]," +
                "[27.595395483076576,53.92499592333728]," +
                "[27.58631218224764,53.922157641154854]," +
                "[27.58128974586725,53.921877869587234]," +
                "[27.575829438865185,53.92021756346421]," +
                "[27.572696954011917,53.91827798798688]," +
                "[27.56713304668665,53.91474928153339]," +
                "[27.56018981337547,53.91698501925265]," +
                "[27.552088871598247,53.916556324537616]," +
                "[27.54116591066122,53.91426329585554]," +
                "[27.534263245761398,53.910782438314826]," +
                "[27.514407858252525,53.911828345438835]," +
                "[27.508953586220738,53.91442483093433]," +
                "[27.500710822641846,53.91628026729702]," +
                "[27.49452196061611,53.91655217776854]," +
                "[27.47952539473772,53.91523881604097]," +
                "[27.461687028408054,53.91355375845777]]}}," +
                "\"attributes.rooms\":{\"\$in\":[1.0]}}"
        query = query.replace("{", "%7B").replace("}", "%7D")
                .replace("[", "%5B").replace("]", "%5D")
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
}