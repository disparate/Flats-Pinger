package kazarovets.flatspinger.api

import android.content.Context
import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.onliner.OnlinerFlatsResponse
import kazarovets.flatspinger.model.RentType
import kazarovets.flatspinger.model.onliner.OnlinerFlat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


class OnlinerApi constructor(context: Context) {

    companion object {
        val TAG = "OnlinerApi"
        private val BASE_URL = "https://ak.api.onliner.by/"

        private val PARAM_RENT_TYPE = "rent_type[]"

        public val ONE_ROOM = "1_room"
        public val TWO_ROOMS = "2_rooms"
        public val THREE_ROOMS = "3_rooms"
        public val FOUR_ROOMS = "4_rooms"
        public val FIVE_ROOMS = "5_rooms"
        public val SIX_ROOMS = "6_rooms"

        private class OnlinerRentTypes(val oneRoom: String?, val twoRooms: String?, val threeRooms: String?,
                                       val fourRooms: String?, val fiveRooms: String?, val sixRooms: String?)
    }

    val onlinerApiService by lazy { createService() }

    fun getLatestFlats(minCost: Int?, maxCost: Int?, onlyOwner: Boolean, rentTypes: Set<String>): Single<List<OnlinerFlat>> {
        val currency = "USD"
        val owner = if (onlyOwner) true else null //weird onliner api

        val onlinerRentTypes = getRentTypes(rentTypes)
        return onlinerApiService.getFlats(onlinerRentTypes.oneRoom,
                onlinerRentTypes.twoRooms,
                onlinerRentTypes.threeRooms,
                onlinerRentTypes.fourRooms,
                onlinerRentTypes.fiveRooms,
                onlinerRentTypes.sixRooms,
                minCost, maxCost, currency, owner)
                .map {
                    Log.d(TAG, "started parsing flats")
                    ApiManager.GSON.fromJson(it, OnlinerFlatsResponse::class.java)
                }
                .map {
                    Log.d(TAG, "finished parsing flats")
                    it.flatsList
                }
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
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpBuilder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()

        return retrofit.create(OnlinerApiService::class.java)

    }

    private fun getRentTypes(rentTypes: Set<String>): OnlinerRentTypes {
        val noRoomsFilter = rentTypes.isEmpty()

        val has1Room = rentTypes.contains(RentType.FLAT_1_ROOM.name) || noRoomsFilter
        val has2Rooms = rentTypes.contains(RentType.FLAT_2_ROOM.name) || noRoomsFilter
        val has3Rooms = rentTypes.contains(RentType.FLAT_3_ROOM.name) || noRoomsFilter
        val has4Rooms = rentTypes.contains(RentType.FLAT_4_ROOM_OR_MORE.name) || noRoomsFilter


        return OnlinerRentTypes(if (has1Room) ONE_ROOM else null,
                if (has2Rooms) TWO_ROOMS else null,
                if (has3Rooms) THREE_ROOMS else null,
                if (has4Rooms) FOUR_ROOMS else null,
                if (has4Rooms) FIVE_ROOMS else null,
                if (has4Rooms) SIX_ROOMS else null)
    }


}