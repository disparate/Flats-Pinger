package kazarovets.flatspinger.model


interface FlatDetails {
    fun getDescription(): String?

    fun getPhones(): List<String>
}