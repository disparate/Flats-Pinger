package kazarovets.flatspinger.widgets

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.FlatWithStatus
import java.lang.Exception


class FlatInfoWindowAdapter(layoutInflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    private var map = HashMap<Marker, FlatWithStatus>()

    private var contentsView: View = layoutInflater.inflate(R.layout.item_window_flat, null)

    fun addFlat(key: Marker, flat: FlatWithStatus) {
        map.put(key, flat)
    }

    fun getFlat(key: Marker) = map.get(key)

    override fun getInfoContents(marker: Marker?): View? = null

    override fun getInfoWindow(marker: Marker?): View {
        val context = contentsView.context

        if (marker == null) {
            return contentsView
        }

        val flat = map[marker] ?: return contentsView

        val imageView = contentsView.findViewById<ImageView>(R.id.image)
        Glide.clear(imageView)
        if (!TextUtils.isEmpty(flat.imageUrl)) {
            Glide.with(context)
                    .load(flat.imageUrl)
                    .centerCrop()
                    .listener(MarkerListener(marker)).into(imageView)
        }

        val provider = contentsView.findViewById<ImageView>(R.id.provider)
        provider?.setImageResource(flat.provider.drawableRes)

        val source = contentsView.findViewById<TextView>(R.id.site)
        source?.text = flat.source

        val address = contentsView.findViewById<TextView>(R.id.address)
        address?.text = flat.address

        return contentsView
    }

    inner class MarkerListener(val marker: Marker?) : RequestListener<String, GlideDrawable> {

        override fun onException(e: Exception?, model: String?,
                                 target: Target<GlideDrawable>?,
                                 isFirstResource: Boolean): Boolean {
            Log.e(javaClass.simpleName, "Error loading thumbnail!", e)
            return false
        }

        override fun onResourceReady(resource: GlideDrawable?,
                                     model: String?, target: Target<GlideDrawable>?,
                                     isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
            if (!isFromMemoryCache) {
                marker?.showInfoWindow()
            }
            Log.d(javaClass.simpleName, "Resource ready")
            return false
        }
    }
}