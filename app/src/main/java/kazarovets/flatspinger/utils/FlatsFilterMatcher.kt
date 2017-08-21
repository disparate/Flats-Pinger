package kazarovets.flatspinger.utils

import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter


class FlatsFilterMatcher {

    companion object {
        fun matches(filter: FlatFilter?, flat: Flat): Boolean {
            if (filter == null) {
                return true
            }
            return (filter.agencyAllowed || flat.isOwner())
                    && (filter.minCost == null || filter.minCost < flat.getCostInDollars())
                    && (filter.maxCost == null || filter.maxCost > flat.getCostInDollars())
                    && (filter.rentTypes.isEmpty() || filter.rentTypes.contains(flat.getRentType()))
                    && (filter.subwaysIds.isEmpty() || filter.subwaysIds.contains(flat.getNearestSubway()?.id))
                    && (filter.maxDistToSubway == null || filter.maxDistToSubway > flat.getDistanceToSubwayInMeters())
        }
    }
}