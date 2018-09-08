package kazarovets.flatspinger.base.recycler

import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.util.DiffUtil


abstract class BaseRecyclerItemBindingAdapter<DATA : BaseRecyclerItem>
    : BaseBindingAdapter<DATA>(getDefaultAsyncDifferConfig(BaseRecyclerItemDiffCallback<DATA>())) {

    companion object {
        @JvmStatic
        fun <T> getDefaultAsyncDifferConfig(callback: DiffUtil.ItemCallback<T>): AsyncDifferConfig<T> {
            return AsyncDifferConfig.Builder<T>(callback).build()
        }
    }
}


class BaseRecyclerItemDiffCallback<DATA : BaseRecyclerItem> : DiffUtil.ItemCallback<DATA>() {
    override fun areItemsTheSame(oldItem: DATA, newItem: DATA): Boolean {
        return oldItem.areItemsTheSame(newItem)
    }

    override fun areContentsTheSame(oldItem: DATA, newItem: DATA): Boolean {
        return oldItem.areContentsTheSame(newItem)
    }

}