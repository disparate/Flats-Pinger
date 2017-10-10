package kazarovets.flatspinger.utils

import android.text.format.DateUtils
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatDetails
import kazarovets.flatspinger.model.FlatFilter


class FlatsFilterMatcher {

    companion object {
        fun matches(filter: FlatFilter?, flat: Flat): Boolean {
            if (filter == null) {
                return true
            }
            return (filter.agencyAllowed || flat.isOwner()) and
                    (filter.minCost == null || filter.minCost < flat.getCostInDollars()) and
                    (filter.maxCost == null || filter.maxCost > flat.getCostInDollars()) and
                    (filter.rentTypes.isEmpty() || filter.rentTypes.contains(flat.getRentType())) and
                    (filter.subwaysIds.isEmpty() || filter.subwaysIds.contains(flat.getNearestSubway()?.id)) and
                    (filter.maxDistToSubway == null || filter.maxDistToSubway > flat.getDistanceToSubwayInMeters()) and
                    (!filter.allowWithPhotosOnly || flat.hasImages()) and
                    matchesKeywords(flat, filter.keywords) and
                    (flat.getUpdatedTime() > System.currentTimeMillis() - filter.updateDatesAgo * DateUtils.DAY_IN_MILLIS) and
                    (filter.roomNumbers.isEmpty() || filter.roomNumbers.contains(flat.getRentType().name))
        }

        fun matchesKeywords(flat: Flat, keywords: Set<String>): Boolean {
            if (keywords.isEmpty()) {
                return true
            }

            var matchedAll = true
            for (word in keywords) {
                val matchedAdress = flat.getAddress().contains(word.trim(), true)

                var matchedDescription = false
                if (flat is FlatDetails) {
                    matchedDescription = flat.getDescription()?.contains(word.trim(), true) ?: false
                }

                matchedAll = matchedAll and (matchedAdress or matchedDescription)
            }

            return matchedAll

        }

    }
}