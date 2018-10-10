package kazarovets.flatspinger.utils

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BaseTarget

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

fun ImageView.load(url: String?, placeholderId: Int) {
    Glide.with(context)
            .load(url)
            .apply(RequestOptions()
                    .placeholder(placeholderId)
                    .dontAnimate())
            .into(this)
}

fun ImageView.clearLoadings() {
    Glide.with(this.context).clear(this)
}