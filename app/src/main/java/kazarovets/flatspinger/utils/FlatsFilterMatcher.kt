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
            return (filter.agencyAllowed || flat.isOwner())
                    && (filter.minCost == null || filter.minCost < flat.getCostInDollars())
                    && (filter.maxCost == null || filter.maxCost > flat.getCostInDollars())
                    && (filter.rentTypes.isEmpty() || filter.rentTypes.contains(flat.getRentType()))
                    && (filter.subwaysIds.isEmpty() || filter.subwaysIds.contains(flat.getNearestSubway()?.id))
                    && (filter.maxDistToSubway == null || filter.maxDistToSubway > flat.getDistanceToSubwayInMeters())
                    && (!filter.allowWithPhotosOnly || flat.hasImages())
                    && (flat.getUpdatedTime() > System.currentTimeMillis() - filter.updateDatesAgo * DateUtils.DAY_IN_MILLIS)
                    && matchesKeywords(flat, filter.keywords)
        }

        fun matchesKeywords(flat: Flat, keywords: Set<String>): Boolean {
            if(keywords.isEmpty()) {
                return true
            }

            var matchedAll = true
            for (word in keywords) {
                var matchedAdress = false
                if (flat.getAddress().contains(word, true)) {
                    matchedAdress = true
                }

                var matchedDescription = false
                if (flat is FlatDetails) {
                    matchedDescription = flat.getDescription()?.contains(word, true) ?: false
                }

                matchedAll = matchedAll and (matchedAdress or matchedDescription)
            }

            return matchedAll

        }
    }
}