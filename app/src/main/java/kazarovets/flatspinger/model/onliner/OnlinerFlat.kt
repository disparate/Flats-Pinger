package kazarovets.flatspinger.model.onliner

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.api.OnlinerApi
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.RentType
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*


data class OnlinerFlat(@SerializedName("id")
                       override val id: String = "",

                       @SerializedName("price")
                       val price: Price? = null,

                       @SerializedName("rent_type")
                       val rentTypeString: String? = null,

                       @SerializedName("location")
                       val location: Location? = null,

                       @SerializedName("photo")
                       override val imageUrl: String? = null,

                       @SerializedName("created_at")
                       val createAt: String? = null,

                       @SerializedName("last_time_up")
                       val lastTimeUp: String? = null,

                       @SerializedName("url")
                       override val originalUrl: String? = null,

                       @SerializedName("contact")
                       val contact: Contact? = null) : Flat {

    companion object {
        val FORMAT_TIME = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
    }


    override fun toString(): String = "OnlinerFlat: address = $address"

    override fun equals(other: Any?): Boolean {
        if (other is Flat?) {
            return TextUtils.equals(other?.originalUrl, originalUrl)
        }
        return false
    }

    override fun hashCode(): Int = originalUrl?.hashCode() ?: 0

    override val address by lazy { location?.address ?: "" }

    override val costInDollars by lazy { price?.amount?.toInt() ?: 0 }

    override val latitude by lazy { location?.latitude }

    override val longitude by lazy { location?.longitude }

    override val isOwner by lazy { contact?.isOwner ?: false }

    override val rentType: RentType
        get() {
            return when (rentTypeString) {
                OnlinerApi.ONE_ROOM -> RentType.FLAT_1_ROOM
                OnlinerApi.TWO_ROOMS -> RentType.FLAT_2_ROOM
                OnlinerApi.THREE_ROOMS -> RentType.FLAT_3_ROOM
                OnlinerApi.FOUR_ROOMS, OnlinerApi.FIVE_ROOMS, OnlinerApi.SIX_ROOMS -> RentType.FLAT_4_ROOM_OR_MORE
                else -> RentType.NONE
            }
        }

    override val provider = Provider.ONLINER

    override val updatedTime by lazy { lastTimeUp?.let { FORMAT_TIME.parse(it).time } ?: 0 }

    override val createdTime by lazy { createAt?.let { FORMAT_TIME.parse(it).time } ?: 0 }

    override val source = "onliner.by"

    override val images by lazy { listOfNotNull(imageUrl) }

    override val description = ""

    override val phones = emptyList<String>()

    data class Price(@SerializedName("amount")
                     val amount: Double? = null,

                     @SerializedName("currency")
                     val currency: String? = null) : Serializable

    data class Location(@SerializedName("address")
                        val address: String? = null,

                        @SerializedName("user_address")
                        val userAddress: String? = null,

                        @SerializedName("latitude")
                        val latitude: Double? = null,

                        @SerializedName("longitude")
                        val longitude: Double? = null) : Serializable

    data class Contact(@SerializedName("owner")
                       val isOwner: Boolean? = null) : Serializable
}