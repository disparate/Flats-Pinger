package kazarovets.flatspinger.base.recycler

import android.content.Context

class ClickItem<T>(
    val context: Context,
    val holder: BindableViewHolder<*>,
    val position: Int,
    val item: T
)