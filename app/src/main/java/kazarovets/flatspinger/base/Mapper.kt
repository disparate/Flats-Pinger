package kazarovets.flatspinger.base


interface Mapper<FROM, TO> {
    fun mapFrom(from: FROM): TO
}