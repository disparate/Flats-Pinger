package kazarovets.flatspinger.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class INeedAFlatFlat : Flat, FlatDetails {

    @SerializedName("id")
    val idText: String? = null

    @SerializedName("url")
    val url: String? = null

    @SerializedName("created")
    val createdAd: Long? = null

    @SerializedName("posted")
    val postedAt: Long? = null

    @SerializedName("updated")
    val updatedAt: Long? = null

    @SerializedName("active")
    val active: Boolean? = null

    @SerializedName("valid")
    val valid: Boolean? = null

    @SerializedName("regionId")
    val regionId: Int? = null

    @SerializedName("attributes")
    val attributes: Attributes? = null

    @SerializedName("agent")
    val agent: Agent? = null


    override fun getId(): String {
        return idText ?: ""
    }

    override fun getImageUrl(): String? {
        return if (attributes?.images != null && attributes.images.isNotEmpty()) attributes.images.get(0) else null
    }

    override fun getAddress(): String = attributes?.address ?: ""

    override fun getCostInDollars(): Int = attributes?.price?.value ?: 0

    override fun getOriginalUrl(): String? = url

    override fun getLatitude(): Double? {
        if (attributes?.geoCoordinates != null && attributes.geoCoordinates.size >= 2) {
            return attributes.geoCoordinates[1]
        }
        return null
    }

    override fun getLongitude(): Double? {
        if (attributes?.geoCoordinates != null && attributes.geoCoordinates.size >= 2) {
            return attributes.geoCoordinates[0]
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

    override fun getCreatedTime(): Long = createdAd ?: 0

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

    class Attributes : Serializable {
        @SerializedName("title")
        val title: String? = null

        @SerializedName("previewImg")
        val previewImg: String? = null

        @SerializedName("price")
        val price: Price? = null

        @SerializedName("rooms")
        val rooms: Int? = null

        @SerializedName("city")
        val city: String? = null

        @SerializedName("source")
        val source: String? = null

        @SerializedName("phonesSource")
        val phonesSource: List<String>? = null

        @SerializedName("phones")
        val phones: List<String>? = null

        @SerializedName("description")
        val description: String? = null

        @SerializedName("geoCoordinates")
        val geoCoordinates: List<Double>? = null

        @SerializedName("address")
        val address: String? = null

        @SerializedName("images")
        val images: List<String>? = null

        @SerializedName("agencyName")
        val agencyName: String? = null

        class Price : Serializable {
            @SerializedName("value")
            val value: Int? = null

            @SerializedName("currency")
            val currency: String? = null
        }
    }

    class Agent : Serializable {
        @SerializedName("id")
        val id: String? = null

        @SerializedName("primaryName")
        val primaryName: String? = null
    }

}