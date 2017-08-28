package kazarovets.flatspinger.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kazarovets.flatspinger.R
import kazarovets.flatspinger.db.FlatsDatabase
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.utils.StringsUtils
import kazarovets.flatspinger.utils.SubwayUtils
import java.util.concurrent.TimeUnit


class FlatsRecyclerAdapter(var flats: MutableList<Flat>)
    : RecyclerView.Adapter<FlatsRecyclerAdapter.FlatsViewHolder>() {

    var onClickListener: OnItemClickListener? = null

    override fun onBindViewHolder(holder: FlatsViewHolder?, position: Int) {
        val flat = flats.get(position)
        if (holder != null) {
            val context = holder.itemView.context
            holder.flat = flat

            Glide.clear(holder.imageView)
            if (!TextUtils.isEmpty(flat.getImageUrl())) {
                Glide.with(holder.itemView.context).load(flat.getImageUrl()).centerCrop().into(holder.imageView)
            }

            holder.costView?.text = "${flat.getCostInDollars()}$"
            val latitude = flat.getLatitude()
            val longitude = flat.getLongitude()
            if (latitude != null && longitude != null) {
                holder.subwayView?.text = "${SubwayUtils.getNearestSubway(latitude, longitude).name}" +
                        " (${flat.getDistanceToSubwayInMeters().toInt()}${context.getString(R.string.meter_small)})"
            }
            holder.agencyLine?.visibility = if (flat.isOwner()) View.INVISIBLE else View.VISIBLE
            holder.provider?.setImageResource(flat.getProvider().drawableRes)
            holder.source?.text = flat.getSource()

            holder.updatedTime?.text = StringsUtils.getTimeAgoString(flat.getUpdatedTime(), context)

            val isFavorite = FlatsDatabase.getInstance(context)
                    .getFlatStatus(flat.getId(), flat.getProvider()) == FlatStatus.FAVORITE
            setFavoriteIcon(holder.favoriteIcon, isFavorite)
            holder.favoriteIcon?.isSelected = isFavorite
            holder.favoriteIcon?.setOnClickListener {
                val imageView = it as ImageView
                val db = FlatsDatabase.getInstance(context)
                imageView.isSelected = !imageView.isSelected
                if (imageView.isSelected) {
                    db.setFavoriteFlat(flat.getId(), flat.getProvider())
                } else {
                    db.setSeenFlat(flat.getId(), flat.getProvider())
                }
                imageView.isSelected
                setFavoriteIcon(imageView, imageView.isSelected)
            }
        }
    }

    private fun setFavoriteIcon(favoriteIcon: ImageView?, isFavorite: Boolean) {
        favoriteIcon?.setImageResource(if (isFavorite) R.drawable.ic_star_24dp else R.drawable.ic_star_border_24dp)
    }

    private fun setTimeAgo(timeAgoView: TextView?, flat: Flat, context: Context) {
        var diff = System.currentTimeMillis() - flat.getUpdatedTime()
        val days = TimeUnit.MILLISECONDS.toDays(diff)
        diff -= days * DateUtils.DAY_IN_MILLIS
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        diff -= hours * DateUtils.HOUR_IN_MILLIS
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val showDays = days > 0
        val showHours = hours > 0 && (!showDays || days < 2)
        val showMinutes = days < 1 && hours < 1
        timeAgoView?.text = "${if (showDays) "${days}${context.getString(R.string.day_small)}" else ""} " +
                "${if (showHours) "${hours}${context.getString(R.string.hour_small)}" else ""}" +
                "${if (showMinutes) "${minutes}${context.getString(R.string.minute_small)}" else ""}" +
                " " + context.getString(R.string.time_ago)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): FlatsViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.list_item_flat, parent, false)
        val holder = FlatsViewHolder(view)
        holder.itemView.setOnClickListener {
            val flat = holder.flat
            if (flat != null) {
                onClickListener?.onItemClick(flat)
            }
        }
        return holder
    }

    override fun getItemCount(): Int {
        return flats.size
    }

    fun setData(data: List<Flat>?) {
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