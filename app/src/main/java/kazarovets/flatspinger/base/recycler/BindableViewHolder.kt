package kazarovets.flatspinger.base.recycler


import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class BindableViewHolder<out BINDING : ViewDataBinding>(val bindings: BINDING) : RecyclerView.ViewHolder(bindings.root) {

    companion object {

        fun <BINDING : ViewDataBinding> create(inflater: LayoutInflater,
                                               layoutResId: Int, parent: ViewGroup): BindableViewHolder<BINDING> {
            return BindableViewHolder(DataBindingUtil.inflate<BINDING>(inflater, layoutResId, parent, false))
        }
    }

    val context: Context get() = itemView.context
}