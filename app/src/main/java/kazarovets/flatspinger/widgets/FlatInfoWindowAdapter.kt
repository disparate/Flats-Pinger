package kazarovets.flatspinger.widgets

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.utils.loadCenterCrop


class FlatInfoWindowAdapter(layoutInflater: LayoutInflater, appContext: Context) : GoogleMap.InfoWindowAdapter {

    private var map = HashMap<Marker, FlatWithStatus>()

    private var contentsView: View = layoutInflater.inflate(R.layout.item_window_flat, null)

    private var clickedMarkerWithBitmap: Pair<Marker, Bitmap>? = null

    private val flatWidth = appContext.resources.getDimensionPixelOffset(R.dimen.map_flat_width)
    private val flatHeight = appContext.resources.getDimensionPixelOffset(R.dimen.map_flat_height)

    fun addFlat(key: Marker, flat: FlatWithStatus) {
        map[key] = flat
    }

    fun getFlat(key: Marker) = map[key]

    override fun getInfoContents(marker: Marker?): View? = null

    override fun getInfoWindow(marker: Marker?): View {
        val flat = map[marker]
        if (marker == null || flat == null) return contentsView

        setImage(marker, flat)

        val provider = contentsView.findViewById<ImageView>(R.id.provider)
        provider?.setImageResource(flat.provider.drawableRes)

        val source = contentsView.findViewById<TextView>(R.id.site)
        source?.text = flat.source

        val address = contentsView.findViewById<TextView>(R.id.address)
        address?.text = flat.address

        return contentsView
    }

    private fun setImage(marker: Marker, flat: FlatWithStatus) {

        val imageView = contentsView.findViewById<ImageView>(R.id.image)
        val clickedMarker = clickedMarkerWithBitmap?.first
        val clickedMarkerBitmap = clickedMarkerWithBitmap?.second
        if (clickedMarker == marker && clickedMarkerBitmap != null) {
            imageView.setImageBitmap(clickedMarkerBitmap)
        } else {
            try {
                imageView.setImageBitmap(null)
                imageView.loadCenterCrop(flat.imageUrl, MarkerTarget(marker))
            } catch (ignored: Exception) {
                //happens if triggered by target
            }
        }
    }

    inner class MarkerTarget(private val marker: Marker) : BaseTarget<Bitmap>() {
        override fun getSize(cb: SizeReadyCallback) {
            cb.onSizeReady(flatWidth, flatHeight)
        }

        override fun removeCallback(cb: SizeReadyCallback) {
            //To change body of created functions use File | Settings | File Templates.
        }

        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            clickedMarkerWithBitmap = Pair(marker, resource)
            marker.showInfoWindow()
        }
    }
}