package kazarovets.flatspinger.base.recycler


interface BaseRecyclerItem {

    fun areItemsTheSame(obj: Any): Boolean

    fun areContentsTheSame(obj: Any): Boolean
}