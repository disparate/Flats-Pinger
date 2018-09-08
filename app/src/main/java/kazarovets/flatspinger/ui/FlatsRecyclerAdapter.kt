package kazarovets.flatspinger.ui

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.utils.StringsUtils
import kazarovets.flatspinger.utils.SubwayUtils


class FlatsRecyclerAdapter(var flats: MutableList<FlatInfo>,
                           private val onFavoriteChangedListener: (Flat, Boolean) -> Unit,
                           private val onRemoveClickListener: (Flat) -> Unit)
    : RecyclerView.Adapter<FlatsRecyclerAdapter.FlatsViewHolder>() {

    var onClickListener: OnItemClickListener? = null

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FlatsViewHolder, position: Int) {
        val flat = flats[position]
        val context = holder.itemView.context
        holder.flat = flat

        Glide.clear(holder.imageView)
        if (!TextUtils.isEmpty(flat.imageUrl)) {
            Glide.with(holder.itemView.context).load(flat.imageUrl).centerCrop().into(holder.imageView)
        }

        holder.costView?.text = "${flat.costInDollars}$"
        val latitude = flat.latitude
        val longitude = flat.longitude
        if (latitude != null && longitude != null) {
            holder.subwayView?.text = SubwayUtils.getNearestSubway(latitude, longitude).name +
                    " (${flat.getDistanceToSubwayInMeters().toInt()}${context.getString(R.string.meter_small)})"
        }
        holder.agencyLine?.visibility = if (flat.isOwner) View.INVISIBLE else View.VISIBLE
        holder.provider?.setImageResource(flat.provider.drawableRes)
        holder.source?.text = flat.source

        holder.updatedTime?.text = StringsUtils.getTimeAgoString(flat.updatedTime, context)

        val isFavorite = flat.status == FlatStatus.FAVORITE
        setFavoriteIcon(holder.favoriteIcon, isFavorite)
        holder.favoriteIcon?.isSelected = isFavorite
        holder.favoriteIcon?.setOnClickListener {
            val imageView = it as ImageView
            imageView.isSelected = !imageView.isSelected
            onFavoriteChangedListener.invoke(flat, imageView.isSelected)
            setFavoriteIcon(imageView, imageView.isSelected)
        }
    }

    private fun setFavoriteIcon(favoriteIcon: ImageView?, isFavorite: Boolean) {
        favoriteIcon?.setImageResource(if (isFavorite) R.drawable.ic_star_24dp else R.drawable.ic_star_border_24dp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlatsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_flat, parent, false)
        val holder = FlatsViewHolder(view)
        holder.itemView.setOnClickListener {
            holder.flat?.let { onClickListener?.onItemClick(it) }
        }

        holder.itemView.findViewById<View>(R.id.close).setOnClickListener {
            holder.flat?.let { onRemoveClickListener.invoke(it) }
            removeItem(holder.adapterPosition)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return flats.size
    }

    fun setData(data: List<FlatInfo>?) {
        if (data != null) {
            flats.clear()
            flats.addAll(data)
            notifyDataSetChanged()
        }
    }

    fun getItem(pos: Int): Flat? = flats.getOrNull(pos)

    fun removeItem(position: Int) {
        flats.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, flats.size)
    }


    class FlatsViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView? = null
        var costView: TextView? = null
        var subwayView: TextView? = null
        var agencyLine: View? = null
        var provider: ImageView? = null
        var updatedTime: TextView? = null
        var source: TextView? = null
        var favoriteIcon: ImageView? = null

        var flat: Flat? = null

        init {
            imageView = itemView?.findViewById(R.id.image)
            costView = itemView?.findViewById(R.id.cost)
            subwayView = itemView?.findViewById(R.id.subway_name)
            agencyLine = itemView?.findViewById(R.id.agent_line)
            provider = itemView?.findViewById(R.id.provider)
            updatedTime = itemView?.findViewById(R.id.update_time)
            source = itemView?.findViewById(R.id.site)
            favoriteIcon = itemView?.findViewById(R.id.favorite_icon)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: Flat)
    }

}