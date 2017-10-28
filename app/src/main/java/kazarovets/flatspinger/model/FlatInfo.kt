package kazarovets.flatspinger.model


interface FlatInfo : Flat {

    val status : FlatStatus

    val isSeen : Boolean
}