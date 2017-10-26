package kazarovets.flatspinger.model.ineedaflat

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatDetails
import kazarovets.flatspinger.model.Provider
import kazarovets.flatspinger.model.RentType
import java.io.Serializable


@Entity
data class INeedAFlatFlat(@PrimaryKey @SerializedName("id") @NonNull var idText: String = "",
                          @SerializedName("url") var url: String? = null,
                          @SerializedName("created") var createdAt: Long? = null,
                          @SerializedName("posted") var postedAt: Long? = null,
                          @SerializedName("updated") var updatedAt: Long? = null,
                          @SerializedName("active") var active: Boolean? = null,
                          @SerializedName("valid") var valid: Boolean? = null,
                          @SerializedName("regionId") var regionId: Int? = null,
                          @Embedded @SerializedName("attributes") var attributes: Attributes? = null,
                          @Embedded @SerializedName("agent") var agent: Agent? = null) : Flat, FlatDetails {


    override fun getId() = idText

    override fun getImageUrl(): String? =
            if (attributes?.images.orEmpty().isNotEmpty()) attributes?.images?.get(0) else null

    override fun getAddress(): String = attributes?.address ?: ""

    override fun getCostInDollars(): Int = attributes?.price?.value ?: 0

    override fun getOriginalUrl(): String? = url

    override fun getLatitude(): Double? {
        if (attributes?.geoCoordinates?.size ?: 0 >= 2) {
            return attributes?.geoCoordinates?.get(1)
        }
        return null
    }

    override fun getLongitude(): Double? {
        if (attributes?.geoCoordinates?.size ?: 0 >= 2) {
            return attributes?.geoCoordinates?.get(0)
        }
        return null
    }

    override fun isOwner(): Boolean = agent == null && attributes?.agencyName.isNullOrEmpty()

    override fun getRentType(): RentType = when (attributes?.rooms) {
        1 -> RentType.FLAT_1_ROOM
        2 -> RentType.FLAT_2_ROOM
        3 -> RentType.FLAT_3_ROOM
        4 -> RentType.FLAT_4_ROOM_OR_MORE
        else -> RentType.NONE
    }

    override fun getProvider(): Provider = Provider.I_NEED_A_FLAT

    override fun getUpdatedTime(): Long = updatedAt ?: 0

    override fun getCreatedTime(): Long = createdAt ?: 0

    override fun getSource(): String = attributes?.source ?: "ineedaflat.by"

    override fun equals(other: Any?): Boolean {
        if (other is Flat?) {
            return TextUtils.equals(other?.getOriginalUrl(), getOriginalUrl())
        }
        return false
    }

    override fun hashCode(): Int = getOriginalUrl()?.hashCode() ?: 0

    fun getImages(): List<String> = attributes?.images ?: emptyList()

    override fun getDescription(): String? = attributes?.description

    override fun getPhones(): List<String> = attributes?.phones.orEmpty()

    override fun toString(): String = "INeedAFlatFlat: address = ${getAddress()}, desc = ${getDescription()}"


    data class Attributes(@SerializedName("title") var title: String? = null,
                          @SerializedName("previewImg") var previewImg: String? = null,
                          @Embedded @SerializedName("price") var price: Price? = null,
                          @SerializedName("rooms") var rooms: Int? = null,
                          @SerializedName("city") var city: String? = null,
                          @SerializedName("source") var source: String? = null,
                          @SerializedName("phonesSource") var phonesSource: List<String> = emptyList(),
                          @SerializedName("phones") var phones: List<String> = emptyList(),
                          @SerializedName("description") var description: String? = null,
                          @SerializedName("geoCoordinates") var geoCoordinates: List<Double> = emptyList(),
                          @SerializedName("address") var address: String? = null,
                          @SerializedName("images") var images: List<String> = emptyList(),
                          @SerializedName("agencyName") var agencyName: String? = null) : Serializable {


        data class Price(@SerializedName("value") var value: Int? = null,
                         @SerializedName("currency") var currency: String? = null) : Serializable

    }

    data class Agent(
            @SerializedName("id") var id: String = "",
            @SerializedName("primaryName") var primaryName: String? = null) : Serializable

}