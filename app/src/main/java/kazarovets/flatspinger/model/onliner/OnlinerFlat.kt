package kazarovets.flatspinger.model.onliner

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.api.OnlinerApi
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.RentType
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


@Entity
data class OnlinerFlat(@PrimaryKey
                       @SerializedName("id")
                       @NonNull
                       var idText: String = "",

                       @Embedded
                       @SerializedName("price")
                       var price: Price? = null,

                       @SerializedName("rent_type")
                       var rentType: String? = null,

                       @Embedded
                       @SerializedName("location")
                       var location: Location? = null,

                       @SerializedName("photo")
                       var photoUrl: String? = null,

                       @SerializedName("created_at")
                       var createAt: String? = null,

                       @SerializedName("last_time_up")
                       var lastTimeUp: String? = null,

                       @SerializedName("url")
                       var siteUrl: String? = null,

                       @Embedded
                       @SerializedName("contact")
                       var contact: Contact? = null) : Flat {

    companion object {
        val FORMAT_TIME = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
    }


    override fun toString(): String = "OnlinerFlat: address = ${getAddress()}"

    override fun equals(other: Any?): Boolean {
        if (other is Flat?) {
            return TextUtils.equals(other?.getOriginalUrl(), getOriginalUrl())
        }
        return false
    }

    override fun hashCode(): Int = getOriginalUrl()?.hashCode() ?: 0

    override fun getId(): String = idText

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


    data class Price(@SerializedName("amount")
                     var amount: Double? = null,

                     @SerializedName("currency")
                     var currency: String? = null) : Serializable

    data class Location(@SerializedName("address")
                        var address: String? = null,

                        @SerializedName("user_address")
                        var userAddress: String? = null,

                        @SerializedName("latitude")
                        var latitude: Double? = null,

                        @SerializedName("longitude")
                        var longitude: Double? = null) : Serializable

    data class Contact(@SerializedName("owner")
                       var isOwner: Boolean? = null) : Serializable
}