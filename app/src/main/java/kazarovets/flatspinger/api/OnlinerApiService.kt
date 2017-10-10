package kazarovets.flatspinger.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface OnlinerApiService {

    @GET("search/apartments?metro%5B%5D=red_line&metro%5B%5D=blue_line")
    fun getFlats(@Query("rent_type[]") rent1k: String?,
                 @Query("rent_type[]") rent2k: String?,
                 @Query("rent_type[]") rent3k: String?,
                 @Query("rent_type[]") rent4k: String?,
                 @Query("rent_type[]") rent5k: String?,
                 @Query("rent_type[]") rent6k: String?,
                 @Query("price[min]") priceMin: Int?,
                 @Query("price[max]") priceMax: Int?,
                 @Query("currency") currency: String,
                 @Query("only_owner") owner: Boolean?): Single<String>
}