package kazarovets.flatspinger.flats.adapter

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import kazarovets.flatspinger.R
import kazarovets.flatspinger.utils.clearLoadings
import kazarovets.flatspinger.utils.loadCenterCrop


@BindingAdapter("loadCropImg")
fun loadCropImg(imageView: ImageView, url: String?) {
    val prevUrl = imageView.getTag(R.id.glide_image_url)
    if (url != prevUrl) {
        imageView.clearLoadings()
        if (url != null && url.isNotEmpty()) {
            imageView.setTag(R.id.glide_image_url, url)
            imageView.loadCenterCrop(url)
        }
    }
}

@BindingAdapter("setImgRes")
fun setImgRes(imageView: ImageView, res: Int?) {
    res?.let {
        imageView.setImageResource(it)
    }
}

@BindingAdapter("isVisible")
fun setIsVisible(view: View, visible: Boolean?) {
    if (visible != false) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}


@BindingAdapter("isInvisible")
fun setIsInvisible(view: View, invisible: Boolean?) {
    if (invisible == true) {
        view.visibility = View.INVISIBLE
    } else {
        view.visibility = View.VISIBLE
    }
}
