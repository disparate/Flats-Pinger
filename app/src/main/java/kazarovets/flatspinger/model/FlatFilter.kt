package kazarovets.flatspinger.model


data class FlatFilter(val minCost: Int = 0,
                      val maxCost: Int = 0,
                      val agencyAllowed: Boolean = true,
                      val closeToSubway: Boolean = true,
                      val subwaysIds: MutableSet<Int> = HashSet(),
                      val rentTypes: MutableSet<RentType> = HashSet()) {
}