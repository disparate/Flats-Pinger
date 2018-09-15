package kazarovets.flatspinger.flats.adapter

import kazarovets.flatspinger.base.recycler.BaseRecyclerItem
import kazarovets.flatspinger.model.FlatWithStatus


data class FlatViewState(val flat: FlatWithStatus,
                         val imageUrl: String?,
                         val cost: String,
                         val nearestSubwayNameAndDistance: String,
                         val showAgencyLine: Boolean,
                         val providerRes: Int,
                         val sourceName: String,
                         val updatedTime: String,

                         val isSeen: Boolean,
                         val isFavorite: Boolean) : BaseRecyclerItem {

    override fun areItemsTheSame(obj: Any): Boolean {
        return obj is FlatViewState
                && obj.flat.originalUrl == this.flat.originalUrl
    }

    override fun areContentsTheSame(obj: Any): Boolean {
        return areItemsTheSame(obj)
                && obj is FlatViewState
                && imageUrl == obj.imageUrl
                && nearestSubwayNameAndDistance == obj.nearestSubwayNameAndDistance
                && showAgencyLine == obj.showAgencyLine
                && providerRes == obj.providerRes
                && sourceName == obj.sourceName
                && updatedTime == obj.updatedTime
                && isSeen == obj.isSeen
                && isFavorite == obj.isFavorite

    }
}
