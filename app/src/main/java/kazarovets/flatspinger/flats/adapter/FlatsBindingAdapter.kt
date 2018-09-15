package kazarovets.flatspinger.flats.adapter

import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import kazarovets.flatspinger.R


@BindingAdapter("loadCropImg")
fun loadCropImg(imageView: ImageView, url: String?) {
    val prevUrl = imageView.getTag(R.id.glide_image_url)
    if (url != prevUrl) {
        Glide.clear(imageView)
        if (url != null && url.isNotEmpty()) {
            imageView.setTag(R.id.glide_image_url, url)
            Glide.with(imageView.context).load(url).centerCrop().into(imageView)
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
