package kazarovets.flatspinger.model


data class FlatFilter(val minCost: Int? = null,
                      val maxCost: Int? = null,
                      val agencyAllowed: Boolean = true,
                      val allowWithPhotosOnly: Boolean = false,
                      val closeToSubway: Boolean = false,
                      val subwaysIds: Set<Int> = HashSet(),
                      val rentTypes: Set<RentType> = HashSet(),
                      val maxDistToSubway: Double? = null,
                      val keywords: Set<String> = HashSet(),
                      val updateDatesAgo: Int? = null)