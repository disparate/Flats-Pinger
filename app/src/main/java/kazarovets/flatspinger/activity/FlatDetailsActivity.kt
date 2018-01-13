package kazarovets.flatspinger.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kazarovets.flatspinger.R
import kazarovets.flatspinger.db.FlatsDatabase
import kazarovets.flatspinger.fragments.MovableMapsFragment
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.utils.StringsUtils
import kazarovets.flatspinger.views.FlatTagsView
import kotlinx.android.synthetic.main.activity_flat_details.*


class FlatDetailsActivity : AppCompatActivity() {

    companion object {

        val EXTRA_FLAT = "flat"
        fun getCallingIntent(context: Context, flat: Flat): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_FLAT, flat)
            intent.setClass(context, FlatDetailsActivity::class.java)
            return intent
        }

        val MAP_ZOOM = 15.0f
    }

    private lateinit var flat: Flat

    private var mapFragment: MovableMapsFragment? = null

    var isFavorite = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val flat = intent.extras.getSerializable(EXTRA_FLAT) as Flat
        this.flat = flat

        setContentView(R.layout.activity_flat_details)

        setSupportActionBar(detailsToolbar)
        supportActionBar?.title = flat.getAddress()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        detailsFlatImage.visibility = if (flat.hasImages()) View.VISIBLE else View.GONE
        if (!flat.getImageUrl().isNullOrBlank()) {
            Glide.with(this).load(flat.getImageUrl()).centerCrop().into(detailsFlatImage)
        }

        detailsFlatImage.setOnClickListener { startActivity(ImagesActivity.getCallingIntent(this, flat.getImages())) }

        detailsCreatedTimeAgo.text = "${getString(R.string.created)} ${StringsUtils.getTimeAgoString(flat.getCreatedTime(), this)}"

        detailsUpdatedTimeAgo.text = "${getString(R.string.updated)} ${StringsUtils.getTimeAgoString(flat.getUpdatedTime(), this)}"

        detailsCost.text = "${flat.getCostInDollars()}$"

        detailsFlatTags.tags = flat.getTags()

        isFavorite = FlatsDatabase.getInstance(this)
                .getFlatStatus(flat.getId(), flat.getProvider()) == FlatStatus.FAVORITE

        fillDetails()

        setupMap()

        FlatsDatabase.getInstance(this).setSeenFlat(flat.getId(), flat.getProvider())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as MovableMapsFragment?
        mapFragment?.setListener(object : MovableMapsFragment.OnTouchListener {
            override fun onTouch() {
                detailsScrollView.requestDisallowInterceptTouchEvent(true)
            }

        })
        val lat = flat.getLatitude()
        val long = flat.getLongitude()
        if (lat != null && long != null) {
            mapFragment?.getMapAsync {
                val flatsMarker = MarkerOptions()
                        .position(LatLng(lat, long))
                        .title(flat.getAddress())
                it.addMarker(flatsMarker)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), MAP_ZOOM))
                it.uiSettings.isZoomControlsEnabled = true
            }
        } else {
            if (mapFragment != null) {
                supportFragmentManager.beginTransaction().remove(mapFragment).commit()
            }
        }
    }

    private fun fillDetails() {
        val flat = flat
        if (!flat.getDescription().isNullOrEmpty()) {
            detailsDescription.text = flat.getDescription()
        } else {
            detailsDescriptionContainer.visibility = View.GONE
        }
        if (flat.getPhones().isNotEmpty()) {
            detailsPhone.text = flat.getPhones()[0]
        } else {
            detailsPhoneContainer.visibility = View.GONE
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
