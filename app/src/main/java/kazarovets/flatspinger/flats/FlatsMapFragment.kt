package kazarovets.flatspinger.flats

import android.graphics.Point
import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.model.FlatWithStatus
import kazarovets.flatspinger.widgets.FlatInfoWindowAdapter


class FlatsMapFragment : SupportMapFragment() {

    companion object {
        const val MINSK_CENTER_LATITUDE = 53.901976
        const val MINSK_CENTER_LONGITUDE = 27.562056
        const val MAP_ZOOM = 11.5f
        const val SELECT_FLAT_INFO_ANIMATION_LENGTH_MS = 500
    }

    var map: GoogleMap? = null
    private var infoWindowAdapter: FlatInfoWindowAdapter? = null
    private var iconGenerator: IconGenerator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoWindowAdapter = FlatInfoWindowAdapter(layoutInflater, context!!.applicationContext)
        iconGenerator = IconGenerator(context)

        getMapAsync {
            map = it
            it.setInfoWindowAdapter(infoWindowAdapter)

            it.moveCameraToCityCenter()

            it.setOnInfoWindowClickListener {
                val flat = infoWindowAdapter?.getFlat(it)
                if (flat != null) {
                    val intent = FlatDetailsActivity.getCallingIntent(context!!, flat)
                    startActivity(intent)
                }
            }

            it.setOnMarkerClickListener()
        }
    }

    private fun GoogleMap.moveCameraToCityCenter() {
        this.moveCamera(CameraUpdateFactory.newLatLngZoom(
                LatLng(MINSK_CENTER_LATITUDE, MINSK_CENTER_LONGITUDE),
                MAP_ZOOM))
    }

    private fun GoogleMap.setOnMarkerClickListener() {
        this.setOnMarkerClickListener {
            val containerHeight = view?.height

            val projection = map?.projection

            val markerLatLng = LatLng(it.position.latitude,
                    it.position.longitude)

            val markerScreenPosition = projection?.toScreenLocation(markerLatLng)

            if (markerScreenPosition != null && containerHeight != null) {
                val pointHalfScreenAbove = Point(markerScreenPosition.x,
                        markerScreenPosition.y - containerHeight / 3)

                val aboveMarkerLatLng = projection
                        .fromScreenLocation(pointHalfScreenAbove)

                it.showInfoWindow()
                val newPos = CameraUpdateFactory.newLatLng(aboveMarkerLatLng)
                map?.animateCamera(newPos, SELECT_FLAT_INFO_ANIMATION_LENGTH_MS, null)
            }
            true
        }
    }

    fun setFlats(flats: List<FlatWithStatus>?) {
        map?.clear()


        if (flats == null)
            return

        fun getIconGeneratorStyle(flat: FlatWithStatus): Int {
            if (flat.status == FlatStatus.FAVORITE) {
                return IconGenerator.STYLE_ORANGE
            }
            if (flat.isSeen) {
                return IconGenerator.STYLE_DEFAULT
            }
            return if (flat.isOwner) IconGenerator.STYLE_BLUE else IconGenerator.STYLE_RED
        }

        for (flat in flats) {
            val lat = flat.latitude
            val long = flat.longitude
            if (lat != null && long != null) {
                iconGenerator?.setStyle(getIconGeneratorStyle(flat))
                val markerOptions = MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                iconGenerator?.makeIcon("${flat.costInDollars}$")))
                        .position(LatLng(lat, long))
                val flatsMarker = map?.addMarker(markerOptions)

                if (flatsMarker != null) {
                    infoWindowAdapter?.addFlat(flatsMarker, flat)
                }
            }
        }

    }

}