package kazarovets.flatspinger.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import kazarovets.flatspinger.R
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.INeedAFlatFlat

class FlatDetailsActivity : AppCompatActivity() {

    companion object {

        val EXTRA_FLAT = "flat"
        fun getCallingIntent(context: Context, flat: Flat): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_FLAT, flat)
            intent.setClass(context, FlatDetailsActivity::class.java)
            return intent
        }
    }

    private var flatImage: ImageView? = null
    private var openInChrome: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flat = intent.extras.getSerializable(EXTRA_FLAT) as Flat

        setContentView(R.layout.activity_flat_details)
        flatImage = findViewById(R.id.flat_image)
        flatImage?.visibility = if(flat.hasImages()) View.VISIBLE else View.GONE
        if(!flat.getImageUrl().isNullOrBlank()) {
            Glide.with(this).load(flat.getImageUrl()).centerCrop().into(flatImage)
        }
        if(flat is INeedAFlatFlat) {
            flatImage?.setOnClickListener { startActivity(ImagesActivity.getCallingIntent(this, flat.getImages())) }
        }

        openInChrome = findViewById(R.id.open_in_web_button)
        openInChrome?.setOnClickListener {
            if (!TextUtils.isEmpty(flat.getOriginalUrl())) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(flat.getOriginalUrl()))
                startActivity(intent)
            }
        }
    }


}
