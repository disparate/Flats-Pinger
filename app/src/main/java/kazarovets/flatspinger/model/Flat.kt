package kazarovets.flatspinger.model

import kazarovets.flatspinger.utils.SubwayUtils
import java.io.Serializable


interface Flat : Comparable<Flat>, Serializable {

    val id: String

    val imageUrl: String?

    val images: List<String>

    val address: String

    val costInDollars: Int

    val originalUrl: String?

    val latitude: Double?

    val longitude: Double?

    val isOwner: Boolean

    val rentType: RentType

    val provider: Provider

    val updatedTime: Long

    val createdTime: Long

    val source: String

    val description: String

    val phones: List<String>

    fun getNearestSubway(): Subway? {
        val latitude = latitude
        val longitude = longitude
        if (latitude != null && longitude != null) {
            return SubwayUtils.getNearestSubway(latitude, longitude)
        }
        return null
    }

    fun getDistanceToSubwayInMeters(): Double {
        val nearest = getNearestSubway()
        val lat = latitude
        val long = longitude
        return if (nearest != null && lat != null && long != null) {
            SubwayUtils.distanceBetweenInMeters(lat, long, nearest.latitude, nearest.longitude)
        } else {
            Double.MAX_VALUE
        }
    }


    fun hasImages(): Boolean = !imageUrl.isNullOrBlank()

    fun getTags(): List<Tag> = listOf(
            RentTypeTag(rentType),
            OwnerTag(isOwner),
            SubwayTag(getNearestSubway()),
            StringTag(source)
    )

    override fun compareTo(other: Flat): Int {
        if (updatedTime < other.updatedTime) {
            return 1
        } else if (updatedTime == other.updatedTime) {
            return 0
        } else {
            return -1
        }
    }

}