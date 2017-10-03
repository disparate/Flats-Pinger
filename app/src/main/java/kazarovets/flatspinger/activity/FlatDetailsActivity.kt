package kazarovets.flatspinger.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kazarovets.flatspinger.R
import kazarovets.flatspinger.db.FlatsDatabase
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatDetails
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.INeedAFlatFlat
import kazarovets.flatspinger.utils.StringsUtils
import kazarovets.flatspinger.views.FlatTagsView

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
    private var costView: TextView? = null

    private var createdAtTextView: TextView? = null
    private var updatedAtTextView: TextView? = null
    private var flatTagsView: FlatTagsView? = null


    private lateinit var flat: Flat

    var isFavorite = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val flat = intent.extras.getSerializable(EXTRA_FLAT) as Flat
        this.flat = flat

        setContentView(R.layout.activity_flat_details)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = flat.getAddress()

        flatImage = findViewById(R.id.flat_image)
        flatImage?.visibility = if (flat.hasImages()) View.VISIBLE else View.GONE
        if (!flat.getImageUrl().isNullOrBlank()) {
            Glide.with(this).load(flat.getImageUrl()).centerCrop().into(flatImage)
        }
        if (flat is INeedAFlatFlat) {
            flatImage?.setOnClickListener { startActivity(ImagesActivity.getCallingIntent(this, flat.getImages())) }
        }

        createdAtTextView = findViewById(R.id.created_time_ago)
        createdAtTextView?.text = "${getString(R.string.created)} ${StringsUtils.getTimeAgoString(flat.getCreatedTime(), this)}"

        updatedAtTextView = findViewById(R.id.updated_time_ago)
        updatedAtTextView?.text = "${getString(R.string.updated)} ${StringsUtils.getTimeAgoString(flat.getUpdatedTime(), this)}"

        costView = findViewById(R.id.cost)
        costView?.text = "${flat.getCostInDollars()}$"

        flatTagsView = findViewById(R.id.flat_tags)
        flatTagsView?.tags = flat.getTags()

        isFavorite = FlatsDatabase.getInstance(this)
                .getFlatStatus(flat.getId(), flat.getProvider()) == FlatStatus.FAVORITE

        fillDetails()

        FlatsDatabase.getInstance(this).setSeenFlat(flat.getId(), flat.getProvider())
    }

    private fun fillDetails() {
        val descriptionContainer = findViewById<ViewGroup>(R.id.description_container)
        val descriptionTextView = findViewById<TextView>(R.id.description)

        val phoneContainer = findViewById<ViewGroup>(R.id.phone_container)
        val phoneTextView = findViewById<TextView>(R.id.phone)

        val flat = flat
        if(flat is FlatDetails) {
            if(!flat.getDescription().isNullOrEmpty()) {
                descriptionTextView.text = flat.getDescription()
            } else {
                descriptionContainer.visibility = View.GONE
            }
            if(flat.getPhones().isNotEmpty()) {
                phoneTextView.text = flat.getPhones()[0]
            } else {
                phoneContainer.visibility = View.GONE
            }
        } else {
            descriptionContainer.visibility = View.GONE
            phoneContainer.visibility = View.GONE
        }
    }

    private fun openInBrowser() {
        if (!TextUtils.isEmpty(flat.getOriginalUrl())) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(flat.getOriginalUrl()))
            startActivity(intent)
        }
    }

    private fun changeIsFavorite() {
        isFavorite = !isFavorite
        if (isFavorite) {
            FlatsDatabase.getInstance(this).setFavoriteFlat(flat.getId(), flat.getProvider())
        } else {
            FlatsDatabase.getInstance(this).setRegularFlat(flat.getId(), flat.getProvider())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        val favoriteItem = menu?.findItem(R.id.favorite)
        if (isFavorite) {
            favoriteItem?.setIcon(R.drawable.ic_star_white_24dp)
        } else {
            favoriteItem?.setIcon(R.drawable.ic_star_border_white_24dp)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.web -> openInBrowser()
            R.id.favorite -> {
                changeIsFavorite()
                invalidateOptionsMenu()
            }
            else -> return false
        }
        return true
    }


}
