package kazarovets.flatspinger.model


class FlatInfoImpl(val flat: Flat, override val status: FlatStatus,
                   override val isSeen: Boolean) : FlatInfo, Flat by flat