package kazarovets.flatspinger.flats

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kazarovets.flatspinger.R
import kazarovets.flatspinger.activity.ImagesActivity
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.utils.StringsUtils
import kazarovets.flatspinger.utils.extensions.getAppComponent
import kazarovets.flatspinger.utils.loadCenterCrop
import kazarovets.flatspinger.viewmodel.FlatDetailsViewModel
import kazarovets.flatspinger.viewmodel.FlatDetailsViewModelFactory
import kotlinx.android.synthetic.main.activity_flat_details.*
import javax.inject.Inject


class FlatDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var flatsFactory: FlatDetailsViewModelFactory

    private lateinit var detailsViewModel: FlatDetailsViewModel

    private var mapFragment: MovableMapsFragment? = null

    var isFavorite = false

    private val flat by lazy { intent.extras.getSerializable(EXTRA_FLAT) as FlatWithStatus }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        setupViewModel(flat)
        setContentView(R.layout.activity_flat_details)

        setSupportActionBar(detailsToolbar)
        supportActionBar?.title = flat.address
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        detailsFlatImage.visibility = if (flat.hasImages()) View.VISIBLE else View.GONE

        detailsFlatImage.loadCenterCrop(flat.imageUrl)

        detailsFlatImage.setOnClickListener { startActivity(ImagesActivity.getCallingIntent(this, flat.images)) }

        detailsCreatedTimeAgo.text = "${getString(R.string.created)} ${StringsUtils.getTimeAgoString(flat.createdTime, this)}"

        detailsUpdatedTimeAgo.text = "${getString(R.string.updated)} ${StringsUtils.getTimeAgoString(flat.updatedTime, this)}"

        detailsCost.text = "${flat.costInDollars}$"

        detailsFlatTags.tags = flat.getTags()


        setupDetails()

        setupMap()
    }

    private fun setupViewModel(flat: FlatWithStatus) {

        getAppComponent().inject(this)

        detailsViewModel = ViewModelProviders.of(this, flatsFactory)
                .get(FlatDetailsViewModel::class.java)
        detailsViewModel.init(flat)

        detailsViewModel.flatIsFavoriteLiveData.observe(this, Observer {
            this.isFavorite = it == true
            invalidateOptionsMenu()
        })

        detailsViewModel.setSeenFlat(flat)
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
        val lat = flat.latitude
        val long = flat.longitude
        if (lat != null && long != null) {
            mapFragment?.getMapAsync {
                val flatsMarker = MarkerOptions()
                        .position(LatLng(lat, long))
                        .title(flat.address)
                it.addMarker(flatsMarker)
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, long), MAP_ZOOM))
                it.uiSettings.isZoomControlsEnabled = true
            }
        } else if (mapFragment != null) {
            supportFragmentManager.beginTransaction().remove(mapFragment).commit()
        }

    }

    private fun setupDetails() {
        val flat = flat
        detailsPhone.paintFlags = detailsPhone.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        if (!flat.description.isEmpty()) {
            detailsDescription.text = flat.description
        } else {
            detailsDescriptionContainer.visibility = View.GONE
        }
        if (flat.phones.isNotEmpty()) {
            detailsPhone.text = flat.phones[0]
        } else {
            detailsPhoneContainer.visibility = View.GONE
        }

        buttonOpenInBrowser.setOnClickListener { openInBrowser() }
    }

    private fun openInBrowser() {
        if (!TextUtils.isEmpty(flat.originalUrl)) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(flat.originalUrl))
            startActivity(intent)
        }
    }

    private fun shareFlatLink() {
        val url = flat.originalUrl
        if (url == null) {
            //todo: show no url dialog (can it be possible?)
        } else {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.putExtra(Intent.EXTRA_TEXT, url)

            startActivity(Intent.createChooser(share, getString(R.string.share_link_title)))
        }
    }

    private fun changeIsFavorite() {
        detailsViewModel.updateIsFavorite(flat, !isFavorite)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        val favoriteItem = menu?.findItem(R.id.favorite)

        favoriteItem?.setIcon(if (isFavorite) {
            R.drawable.ic_star_white_24dp
        } else {
            R.drawable.ic_star_border_white_24dp
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.share -> shareFlatLink()
            R.id.favorite -> {
                changeIsFavorite()
            }
            else -> return false
        }
        return true
    }


    companion object {

        val EXTRA_FLAT = "flat"

        fun getCallingIntent(context: Context, flat: FlatWithStatus): Intent {
            val intent = Intent()
            intent.putExtra(EXTRA_FLAT, flat)
            intent.setClass(context, FlatDetailsActivity::class.java)
            return intent
        }

        val MAP_ZOOM = 15.0f
    }


}
