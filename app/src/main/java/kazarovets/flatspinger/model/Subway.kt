package kazarovets.flatspinger.model


class Subway(val name: String,
             val latitude: Double,
             val longitude: Double,
             val id: Int) {
    override fun equals(other: Any?): Boolean {
        if(other is Subway?) {
            return other?.id == this.id
        }
        return false
    }

    override fun hashCode(): Int {
        return id
    }
}