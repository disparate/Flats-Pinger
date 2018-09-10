package kazarovets.flatspinger.model


class FlatWithStatusImpl(val flat: Flat,
                         override val status: FlatStatus,
                         override val isSeen: Boolean) : FlatWithStatus, Flat by flat