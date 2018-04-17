package kazarovets.flatspinger.flats

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kazarovets.flatspinger.model.FlatInfo
import kazarovets.flatspinger.model.FlatStatus
import kazarovets.flatspinger.widgets.FlatInfoWindowAdapter


class FlatsMapFragment : SupportMapFragment() {

    companion object {
        val MINSK_CENTER_LATITUDE = 53.901976
        val MINSK_CENTER_LONGITUDE = 27.562056
        val MAP_ZOOM = 11.5f
        val SELECT_FLAT_INFO_ANIMATION_LENGTH_MS = 500
    }

    var map: GoogleMap? = null
    var infoWindowAdapter: FlatInfoWindowAdapter? = null
    var iconGenerator: IconGenerator? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoWindowAdapter = FlatInfoWindowAdapter(layoutInflater)
        iconGenerator = IconGenerator(context)

        getMapAsync {
            map = it
            it.setInfoWindowAdapter(infoWindowAdapter)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(MINSK_CENTER_LATITUDE, MINSK_CENTER_LONGITUDE),
                    MAP_ZOOM))
            it.setOnInfoWindowClickListener {
                val flat = infoWindowAdapter?.getFlat(it)
                if (flat != null) {
                    val intent = FlatDetailsActivity.getCallingIntent(context!!, flat)
                    startActivity(intent)
                }
            }
            it.setOnMarkerClickListener {
                val containerHeight = getView()?.height

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
    }

    fun setFlats(flats: List<FlatInfo>?) {
        map?.clear()


        if (flats == null)
            return

        fun getIconGeneratorStyle(flat: FlatInfo): Int {
            if (flat.status == FlatStatus.FAVORITE) {
                return IconGenerator.STYLE_ORANGE
            }
            if (flat.isSeen) {
                return IconGenerator.STYLE_DEFAULT
            }
            return if (flat.isOwner()) IconGenerator.STYLE_BLUE else IconGenerator.STYLE_RED
        }

        Observable.fromIterable(flats)
                .map { flat ->
                    val lat = flat.getLatitude()
                    val long = flat.getLongitude()
                    return@map if (lat != null && long != null) {
                        val markerOptions = MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(
                                        iconGenerator?.makeIcon("${flat.getCostInDollars()}$")))
                                .position(LatLng(lat, long))
                        Pair(markerOptions, flat)
                    } else {
                        Pair(null, flat)
                    }
                }
                .filter { it.first != null }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val (markerOptions, flat) = it
                    val flatsMarker = map?.addMarker(markerOptions)

                    if (flatsMarker != null) {
                        infoWindowAdapter?.addFlat(flatsMarker, flat)
                    }
                    iconGenerator?.setStyle(getIconGeneratorStyle(flat))

                }, {
                    Log.e("FlatsMap", "error setting icon for flat", it)
                })

        for (flat in flats) {
            val lat = flat.getLatitude()
            val long = flat.getLongitude()
            if (lat != null && long != null) {
                iconGenerator?.setStyle(getIconGeneratorStyle(flat))
                val markerOptions = MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                iconGenerator?.makeIcon("${flat.getCostInDollars()}$")))
                        .position(LatLng(lat, long))
                val flatsMarker = map?.addMarker(markerOptions)

                if (flatsMarker != null) {
                    infoWindowAdapter?.addFlat(flatsMarker, flat)
                }
            }
        }

    }

}