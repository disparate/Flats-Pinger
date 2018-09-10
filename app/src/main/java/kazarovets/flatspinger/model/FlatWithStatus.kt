package kazarovets.flatspinger.model


interface FlatWithStatus : Flat {

    val status : FlatStatus

    val isSeen : Boolean
}