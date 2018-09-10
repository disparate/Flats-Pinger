package kazarovets.flatspinger.model.ineedaflat

import android.support.annotation.NonNull
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.RentType
import java.io.Serializable


data class INeedAFlatFlat(@SerializedName("id") @NonNull override val id: String = "",
                          @SerializedName("url") override val originalUrl: String? = null,
                          @SerializedName("created") override val createdTime: Long = 0,
                          @SerializedName("posted") val postedAt: Long? = null,
                          @SerializedName("updated") override val updatedTime: Long = 0,
                          @SerializedName("active") val active: Boolean? = null,
                          @SerializedName("valid") val valid: Boolean? = null,
                          @SerializedName("regionId") val regionId: Int? = null,
                          @SerializedName("attributes") private val attributes: Attributes? = null,
                          @SerializedName("agent") private val agent: Agent? = null) : Flat {

    override val imageUrl: String?
        get() = if (attributes?.images.orEmpty().isNotEmpty()) attributes?.images?.get(0) else null

    override val address by lazy { attributes?.address ?: "" }

    override val costInDollars by lazy { attributes?.price?.value ?: 0 }

    override val latitude: Double?
        get() {
            if (attributes?.geoCoordinates?.size ?: 0 >= 2) {
                return attributes?.geoCoordinates?.get(1)
            }
            return null
        }

    override val longitude: Double?
        get() {
            if (attributes?.geoCoordinates?.size ?: 0 >= 2) {
                return attributes?.geoCoordinates?.get(0)
            }
            return null
        }

    override val isOwner by lazy { agent == null && attributes?.agencyName.isNullOrEmpty() }

    override val rentType: RentType
        get() {
            return when (attributes?.rooms) {
                1 -> RentType.FLAT_1_ROOM
                2 -> RentType.FLAT_2_ROOM
                3 -> RentType.FLAT_3_ROOM
                4 -> RentType.FLAT_4_ROOM_OR_MORE
                else -> RentType.NONE
            }
        }

    override val provider = Provider.I_NEED_A_FLAT

    override val source by lazy { attributes?.source ?: "ineedaflat.by" }

    override val images by lazy { attributes?.images ?: emptyList() }

    override val description by lazy { attributes?.description ?: "" }

    override val phones by lazy { attributes?.phones.orEmpty() }

    override fun equals(other: Any?): Boolean {
        if (other is Flat?) {
            return TextUtils.equals(other?.originalUrl, originalUrl)
        }
        return false
    }

    override fun hashCode(): Int = originalUrl?.hashCode() ?: 0

    override fun toString(): String = "INeedAFlatFlat: address = ${address}, desc = ${description}"


    data class Attributes(@SerializedName("title") val title: String? = null,
                          @SerializedName("previewImg") val previewImg: String? = null,
                          @SerializedName("price") val price: Price? = null,
                          @SerializedName("rooms") val rooms: Int? = null,
                          @SerializedName("city") val city: String? = null,
                          @SerializedName("source") val source: String? = null,
                          @SerializedName("phonesSource") val phonesSource: List<String> = emptyList(),
                          @SerializedName("phones") val phones: List<String> = emptyList(),
                          @SerializedName("description") val description: String? = null,
                          @SerializedName("geoCoordinates") val geoCoordinates: List<Double> = emptyList(),
                          @SerializedName("address") val address: String? = null,
                          @SerializedName("images") val images: List<String> = emptyList(),
                          @SerializedName("agencyName") val agencyName: String? = null) : Serializable {

        data class Price(@SerializedName("value") val value: Int? = null,
                         @SerializedName("currency") val currency: String? = null) : Serializable

    }

    data class Agent(
            @SerializedName("id") val id: String = "",
            @SerializedName("primaryName") val primaryName: String? = null) : Serializable

}