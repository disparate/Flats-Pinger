package kazarovets.flatspinger.utils

import kazarovets.flatspinger.model.FlatInfo


public fun Iterable<FlatInfo>.filterFlats() = filter {
    val flatsFilter = PreferenceUtils.flatFilter
    FlatsFilterMatcher.matches(flatsFilter, it)
}