package kazarovets.flatspinger.model

import kazarovets.flatspinger.FlatsApplication


data class FlatFilter(val minCost: Int? = null,
                      val maxCost: Int? = null,
                      val agencyAllowed: Boolean = true,
                      val allowWithPhotosOnly: Boolean = false,
                      val closeToSubway: Boolean = true,
                      val subwaysIds: MutableSet<Int> = HashSet(),
                      val rentTypes: MutableSet<RentType> = HashSet(),
                      val maxDistToSubway: Double? = null,
                      val keywords: MutableSet<String> = HashSet(),
                      val roomNumbers: MutableSet<String> = HashSet(),
                      val updateDatesAgo: Int = FlatsApplication.DEFAULT_NUMBER_OF_DAYS_AD_IS_ACTUAL)