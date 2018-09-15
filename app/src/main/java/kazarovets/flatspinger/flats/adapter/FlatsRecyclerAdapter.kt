package kazarovets.flatspinger.flats.adapter

import kazarovets.flatspinger.R
import kazarovets.flatspinger.base.recycler.BaseRecyclerItemBindingAdapter
import kazarovets.flatspinger.base.recycler.BindableViewHolder
import kazarovets.flatspinger.databinding.ListItemFlatBinding
import kazarovets.flatspinger.model.FlatWithStatus


class FlatsRecyclerAdapter(private val onFavoriteChangedListener: (FlatWithStatus, Boolean) -> Unit,
                           private val onRemoveClickListener: (FlatWithStatus) -> Unit,
                           private val onClickListener: (FlatWithStatus) -> Unit)
    : BaseRecyclerItemBindingAdapter<FlatViewState>() {

    override val hasInnerClickListener = false

    override fun onBind(holder: BindableViewHolder<*>, item: FlatViewState) {
        super.onBind(holder, item)

        val flat = item
        if (holder.bindings is ListItemFlatBinding) {
            holder.bindings.close.setOnClickListener {
                flat.let { onRemoveClickListener.invoke(it.flat) }
            }

            holder.bindings.favoriteIcon.setOnClickListener {
                val isFavorite = flat.isFavorite.not()
                changeListElement(flat, flat.copy(isFavorite = isFavorite))
                onFavoriteChangedListener.invoke(flat.flat, isFavorite)
            }

            holder.bindings.root.setOnClickListener {
                onClickListener.invoke(flat.flat)
            }
        }
    }

    override fun getLayoutResId(viewType: Int) = viewType

    override fun getItemViewType(position: Int) = R.layout.list_item_flat

}