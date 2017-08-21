package kazarovets.flatspinger.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


interface INeedAFlatApiService {

    @GET("api/products/?regionId=1&sectionId=2&limit=300")
    fun getFlats(@Query("query", encoded = true) query: String): Single<String>
}