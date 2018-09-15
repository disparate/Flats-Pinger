package kazarovets.flatspinger.model

import java.io.Serializable


interface FlatWithStatus : Flat, Serializable {

    val status: FlatStatus

    val isSeen: Boolean
}