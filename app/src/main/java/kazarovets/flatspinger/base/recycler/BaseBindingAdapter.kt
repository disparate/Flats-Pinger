package kazarovets.flatspinger.base.recycler

import android.content.Context
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kazarovets.flatspinger.BR
import kazarovets.flatspinger.flats.adapter.FlatViewState


abstract class BaseBindingAdapter<DATA>(config: AsyncDifferConfig<DATA>) :
        ListAdapter<DATA, BindableViewHolder<*>>(config) {

    var itemClickListener: ((Context, BindableViewHolder<*>, Int) -> Unit)? = null

    private val clickSubject: PublishSubject<ClickItem<DATA>> = PublishSubject.create()

    val itemClickObservable: Observable<ClickItem<DATA>>
        get() = clickSubject

    open val hasInnerClickListener get() = true

    private val innerClickListener: View.OnClickListener = View.OnClickListener { v ->
        val holder = v.tag as BindableViewHolder<*>
        val position = holder.adapterPosition
        if (position >= 0) { //sometimes it happens
            val clickItem = ClickItem(holder.context, holder, position, getItem(position))
            clickSubject.onNext(clickItem)
            itemClickListener?.invoke(clickItem.context, clickItem.holder, clickItem.position)
        }
    }

    private var currentList: List<DATA>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder<*> =
            BindableViewHolder.create<ViewDataBinding>(
                    LayoutInflater.from(parent.context),
                    getLayoutResId(viewType),
                    parent
            )

    @LayoutRes
    protected abstract fun getLayoutResId(viewType: Int): Int

    override fun onBindViewHolder(holder: BindableViewHolder<*>, position: Int) {
        onBind(holder, getItem(position))
        holder.bindings.executePendingBindings()
        if (hasInnerClickListener) {
            holder.itemView.setOnClickListener(innerClickListener)
        }
        holder.itemView.tag = holder
    }

    public override fun getItem(position: Int): DATA {
        return super.getItem(position)
    }

    open fun onBind(holder: BindableViewHolder<*>, item: DATA) {
        holder.bindings.setVariable(BR.item, item)
    }


    protected fun changeListElement(old: DATA, new: DATA) {
        submitList(currentList?.map {
            if (it == old) {
                new
            } else {
                it
            }
        })
    }

    protected fun removeListElement(element: FlatViewState) {
        submitList(currentList?.filter { it != element })
    }


    override fun submitList(list: List<DATA>?) {
        currentList = list
        super.submitList(list)
    }


}