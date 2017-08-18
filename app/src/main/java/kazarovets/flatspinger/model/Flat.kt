package kazarovets.flatspinger.model


interface Flat {
    fun getImageUrl(): String?

    fun getAddress(): String

    fun getNearestSubwayStation(): String

    fun getCostInDollars(): Int?

    fun getOriginalUrl(): String?

    fun getLatitude(): Double?

    fun getLongitude(): Double?

    fun isOwner(): Boolean
}