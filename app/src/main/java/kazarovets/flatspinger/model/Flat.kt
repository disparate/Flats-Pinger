package kazarovets.flatspinger.model

import kazarovets.flatspinger.utils.SubwayUtils


interface Flat : Comparable<Flat> {

    fun getId(): String

    fun getImageUrl(): String?

    fun getAddress(): String

    fun getNearestSubway(): Subway? {
        val latitude = getLatitude()
        val longitude = getLongitude()
        if (latitude != null && longitude != null) {
            return SubwayUtils.getNearestSubway(latitude, longitude)
        }
        return null
    }

    fun getDistanceToSubwayInMeters(): Double {
        val nearest = getNearestSubway()
        val lat = getLatitude()
        val long = getLongitude()
        if (nearest != null && lat != null && long != null) {
            return SubwayUtils.distanceBetweenInMeters(lat, long, nearest.latitude, nearest.longitude)
        } else {
            return Double.MAX_VALUE
        }
    }

    fun getCostInDollars(): Int

    fun getOriginalUrl(): String?

    fun getLatitude(): Double?

    fun getLongitude(): Double?

    fun isOwner(): Boolean

    fun getRentType(): RentType

    fun getProvider(): Provider

    fun getUpdatedTime(): Long

    fun hasImages(): Boolean = !getImageUrl().isNullOrBlank()

    override fun compareTo(other: Flat): Int {
        if (getUpdatedTime() < other.getUpdatedTime()) {
            return 1
        } else if (getUpdatedTime() == other.getUpdatedTime()) {
            return 0
        } else {
            return -1
        }
    }

}