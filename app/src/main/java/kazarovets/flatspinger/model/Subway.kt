package kazarovets.flatspinger.model


class Subway(val name: String = "",
             val latitude: Double = 0.0,
             val longitude: Double = 0.0,
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