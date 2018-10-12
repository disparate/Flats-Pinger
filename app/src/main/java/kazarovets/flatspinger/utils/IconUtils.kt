package kazarovets.flatspinger.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BaseTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

fun ImageView.loadCenterCrop(url: String?, placeholderId: Int) {
    Glide.with(context)
            .load(url)
            .apply(RequestOptions()
                    .placeholder(placeholderId)
                    .centerCrop()
                    .dontAnimate())
            .into(this)

}


fun ImageView.loadCenterCrop(url: String?, imageViewTarget: BaseTarget<Bitmap>) {
    Glide.with(context)
            .asBitmap()
            .load(url)
            .apply(RequestOptions().centerCrop()
                    .dontAnimate())
            .into(imageViewTarget)
}

fun ImageView.loadFitCenter(url: String?, placeholderId: Int) {
    Glide.with(context)
            .load(url)
            .apply(RequestOptions()
                    .fitCenter()
                    .placeholder(placeholderId)
                    .dontAnimate())
            .into(this)

}

fun ImageView.load(url: String?, onBitmapSet: () -> Unit, onLoadFailed: () -> Unit) {
    Glide.with(context.applicationContext)
            .asBitmap()
            .load(url)
            .apply(RequestOptions().optionalFitCenter())
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    this@load.setImageBitmap(resource)
                    onBitmapSet()
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    onLoadFailed()
                }
            })
}

fun ImageView.clearLoadings() {
    Glide.with(this.context).clear(this)
}