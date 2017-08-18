package kazarovets.flatspinger.model

import com.google.gson.annotations.SerializedName


class OnlinerFlat : Flat {
    override fun getImageUrl(): String? {
        return photoUrl
    }

    override fun getAddress(): String {
        return location?.address ?: ""
    }

    override fun getNearestSubwayStation(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCostInDollars(): Int? {
        return price?.amount?.toInt()
    }

    override fun getOriginalUrl(): String? {
        return siteUrl
    }

    @SerializedName("id")
    val id: Long? = null

    @SerializedName("price")
    val price: Price? = null

    @SerializedName("rent_type")
    val rentType: String? = null

    @SerializedName("location")
    val location: Location? = null

    @SerializedName("photo")
    val photoUrl: String? = null

    @SerializedName("created_at")
    val createAt: String? = null

    @SerializedName("last_time_up")
    val lastTimeUp: String? = null

    @SerializedName("url")
    val siteUrl: String? = null


    class Price {
        @SerializedName("amount")
        val amount: Double? = null

        @SerializedName("currency")
        val currency: String? = null
    }

    class Location {
        @SerializedName("address")
        val address: String? = null

        @SerializedName("user_address")
        val userAddress: String? = null

        @SerializedName("latitude")
        val latitude: Double? = null

        @SerializedName("longitude")
        val longitude: Double? = null
    }
}