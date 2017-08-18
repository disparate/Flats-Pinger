package kazarovets.flatspinger.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface OnlinerApiService {

    @GET("search/apartments?metro%5B%5D=red_line&metro%5B%5D=blue_line")
    fun getFlats(@Query("rent_type[]") rentType: String,
                 @Query("price[min]") priceMin: Int,
                 @Query("price[max]") priceMax: Int,
                 @Query("currency") currency: String): Single<String>
}