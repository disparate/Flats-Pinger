package kazarovets.flatspinger.model


interface FlatInfo : Flat {

    val flat: Flat

    val status : FlatStatus

    val isSeen : Boolean
}