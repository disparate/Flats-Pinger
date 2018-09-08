package kazarovets.flatspinger.api

import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kazarovets.flatspinger.model.RentType
import kazarovets.flatspinger.model.ineedaflat.DBFlat
import kazarovets.flatspinger.model.ineedaflat.INeedAFlatListResponse
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

        private val ONE_ROOM = "1.0"
        private val TWO_ROOMS = "2.0"
        private val THREE_ROOMS = "3.0"
        private val FOUR_ROOMS = "4.0"

        private val rentTypesMap = mapOf(RentType.FLAT_1_ROOM to ONE_ROOM,
                RentType.FLAT_2_ROOM to TWO_ROOMS,
                RentType.FLAT_3_ROOM to THREE_ROOMS,
                RentType.FLAT_4_ROOM_OR_MORE to FOUR_ROOMS)
    }

    val iNeedAFlatApiService by lazy { createApiService() }

    fun getFlats(minCost: Double?, maxCost: Double?, agencyAllowed: Boolean, rentTypes: Set<RentType>): Single<List<DBFlat>> {
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
                getRoomsParam(rentTypes)
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

    private fun getRoomsParam(rentTypes: Set<RentType>): String {
        var list = ArrayList<String?>()
        if (rentTypes.isEmpty()) {
            list = ArrayList(rentTypesMap.values)
        }
        for (rentType in rentTypes) {
            if (rentTypesMap.containsKey(rentType)) {
                list.add(rentTypesMap.get(rentType))
            }
        }
        return "\"attributes.rooms\":{\"\$in\":[${list.joinToString(separator = ",")}]}}"
    }


    private fun createApiService(): INeedAFlatApiService {

        val httpInterceptor = HttpLoggingInterceptor()
        httpInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val okHttpBuilder = OkHttpClient.Builder()
                .addInterceptor(httpInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        return retrofit.create(INeedAFlatApiService::class.java)
    }


}