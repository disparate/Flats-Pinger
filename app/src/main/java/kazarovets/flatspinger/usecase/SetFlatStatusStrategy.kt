package kazarovets.flatspinger.usecase

import kazarovets.flatspinger.db.model.DBFlatInfo
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.model.FlatWithStatusImpl


class SetFlatStatusStrategy {
    fun convertWithStatus(flat: Flat, flatInfos: List<DBFlatInfo>): FlatWithStatus {
        val statuses = flatInfos.filter {
            val sameIds = it.flatId == flat.id && it.provider == flat.provider
            val sameImageUrls = it.imageUrl.isNotBlank() && it.imageUrl == flat.imageUrl
            sameIds || sameImageUrls
        }

        val isSeen = statuses.any { it.isSeen }
        val status = when {
            statuses.any { it.isHidden } -> FlatStatus.HIDDEN
            else -> FlatStatus.REGULAR
        }
        return FlatWithStatusImpl(flat, status, isSeen)
    }
}