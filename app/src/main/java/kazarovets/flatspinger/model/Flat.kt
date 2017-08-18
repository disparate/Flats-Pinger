package kazarovets.flatspinger.model


interface Flat {
    fun getImageUrl(): String?

    fun getAddress(): String

    fun getNearestSubwayStation(): String

    fun getCostInDollars(): Int?

    fun getOriginalUrl(): String?
}