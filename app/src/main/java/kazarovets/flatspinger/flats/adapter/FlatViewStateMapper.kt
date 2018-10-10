package kazarovets.flatspinger.flats.adapter

import android.content.Context
import kazarovets.flatspinger.R
import kazarovets.flatspinger.base.Mapper
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.utils.StringsUtils
import kazarovets.flatspinger.utils.SubwayUtils


class FlatViewStateMapper(val context: Context) : Mapper<FlatWithStatus, FlatViewState> {
    override fun mapFrom(from: FlatWithStatus): FlatViewState {
        return FlatViewState(
                from,
                from.imageUrl,
                "${from.costInDollars}$",
                getNearestSubway(from) ?: "",
                from.isOwner.not(),
                from.provider.drawableRes,
                from.source,
                StringsUtils.getTimeAgoString(from.updatedTime, context),
                from.isSeen && from.status != FlatStatus.FAVORITE,
                from.status == FlatStatus.FAVORITE
        )
    }

    private fun getNearestSubway(flat: FlatWithStatus): String? {
        val latitude = flat.latitude
        val longitude = flat.longitude
        if (latitude == null || longitude == null) return ""
        return SubwayUtils.getNearestSubway(latitude, longitude).name +
                " (${flat.getDistanceToSubwayInMeters().toInt()}${context.getString(R.string.meter_small)})"
    }
}