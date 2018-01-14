package kazarovets.flatspinger.utils

import android.text.format.DateUtils
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatFilter


class FlatsFilterMatcher {

    companion object {
        fun matches(filter: FlatFilter?, flat: Flat): Boolean {
            if (filter == null) {
                return true
            }
            return (filter.agencyAllowed || flat.isOwner()) and
                    (filter.minCost == null || filter.minCost <= flat.getCostInDollars()) and
                    (filter.maxCost == null || filter.maxCost >= flat.getCostInDollars()) and
                    (filter.rentTypes.isEmpty() || filter.rentTypes.contains(flat.getRentType())) and
                    (filter.subwaysIds.isEmpty() || filter.subwaysIds.contains(flat.getNearestSubway()?.id)) and
                    matchesMaxDistanceToSubway(flat, filter.maxDistToSubway, filter.closeToSubway) and
                    (!filter.allowWithPhotosOnly || flat.hasImages()) and
                    matchesKeywords(flat, filter.keywords) and
                    matchesUpdateDate(flat, filter.updateDatesAgo)
        }

        private fun matchesKeywords(flat: Flat, keywords: Set<String>): Boolean {
            if (keywords.isEmpty()) {
                return true
            }

            var matchedAny = false
            for (word in keywords) {
                val matchedAddress = flat.getAddress().contains(word.trim(), true)

                val matchedDescription = flat.getDescription().contains(word.trim(), true)

                matchedAny = matchedAny or (matchedAddress or matchedDescription)
            }

            return matchedAny

        }

        private fun matchesUpdateDate(flat: Flat, updateDatesAgo: Int?): Boolean {
            return updateDatesAgo == null
                    || flat.getUpdatedTime() > System.currentTimeMillis() - updateDatesAgo * DateUtils.DAY_IN_MILLIS
        }

        private fun matchesMaxDistanceToSubway(flat: Flat,
                                               maxDistToSubway: Double?,
                                               closeToSubway: Boolean): Boolean {
            return if (!closeToSubway) {
                true
            } else {
                maxDistToSubway == null || maxDistToSubway >= flat.getDistanceToSubwayInMeters()
            }

        }

    }
}