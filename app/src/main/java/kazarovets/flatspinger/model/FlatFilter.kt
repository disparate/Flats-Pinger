package kazarovets.flatspinger.model


data class FlatFilter(val minCost: Int? = null,
                      val maxCost: Int? = null,
                      val agencyAllowed: Boolean = true,
                      val allowWithPhotosOnly: Boolean = false,
                      val closeToSubway: Boolean = true,
                      val subwaysIds: MutableSet<Int> = HashSet(),
                      val rentTypes: MutableSet<RentType> = HashSet(),
                      val maxDistToSubway: Double? = null) {
}