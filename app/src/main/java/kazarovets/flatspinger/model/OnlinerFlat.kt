package kazarovets.flatspinger.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.api.OnlinerApi
import java.io.Serializable
import java.text.SimpleDateFormat


class OnlinerFlat : Flat {

    companion object {
        val FORMAT_TIME = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    }

    override fun getId(): String = idText ?: ""

    override fun getImageUrl(): String? = photoUrl

    override fun getAddress(): String = location?.address ?: ""

    override fun getCostInDollars(): Int = price?.amount?.toInt() ?: 0

    override fun getOriginalUrl(): String? = siteUrl

    override fun getLatitude(): Double? = location?.latitude

    override fun getLongitude(): Double? = location?.longitude

    override fun isOwner(): Boolean = contact?.isOwner ?: false

    override fun getRentType(): RentType = when (rentType) {
        OnlinerApi.ONE_ROOM -> RentType.FLAT_1_ROOM
        OnlinerApi.TWO_ROOMS -> RentType.FLAT_2_ROOM
        OnlinerApi.THREE_ROOMS -> RentType.FLAT_3_ROOM
        OnlinerApi.FOUR_ROOMS, OnlinerApi.FIVE_ROOMS, OnlinerApi.SIX_ROOMS -> RentType.FLAT_4_ROOM_OR_MORE
        else -> RentType.NONE
    }

    override fun getProvider(): Provider = Provider.ONLINER

    override fun getUpdatedTime(): Long = FORMAT_TIME.parse(lastTimeUp).time

    override fun getCreatedTime(): Long = FORMAT_TIME.parse(createAt).time

    override fun getSource(): String = "onliner.by"

    @SerializedName("id")
    val idText: String? = null

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

    @SerializedName("contact")
    val contact: Contact? = null


    override fun equals(other: Any?): Boolean {
        if (other is Flat?) {
            return TextUtils.equals(other?.getOriginalUrl(), getOriginalUrl())
        }
        return false
    }

    override fun hashCode(): Int = getOriginalUrl()?.hashCode() ?: 0


    class Price : Serializable {
        @SerializedName("amount")
        val amount: Double? = null

        @SerializedName("currency")
        val currency: String? = null
    }

    class Location : Serializable {
        @SerializedName("address")
        val address: String? = null

        @SerializedName("user_address")
        val userAddress: String? = null

        @SerializedName("latitude")
        val latitude: Double? = null

        @SerializedName("longitude")
        val longitude: Double? = null
    }

    class Contact : Serializable {
        @SerializedName("owner")
        val isOwner: Boolean? = null
    }
}