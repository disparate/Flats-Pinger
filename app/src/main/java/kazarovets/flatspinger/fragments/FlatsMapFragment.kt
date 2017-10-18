package kazarovets.flatspinger.fragments

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kazarovets.flatspinger.activity.FlatDetailsActivity
import kazarovets.flatspinger.model.Flat
import kazarovets.flatspinger.widgets.FlatInfoWindowAdapter


class FlatsMapFragment : SupportMapFragment() {

    companion object {
        val MINSK_CENTER_LATITUDE = 53.901976
        val MINSK_CENTER_LONGITUDE = 27.562056
        val MAP_ZOOM = 11.5f
    }

    var map: GoogleMap? = null
    var infoWindowAdapter: FlatInfoWindowAdapter? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoWindowAdapter = FlatInfoWindowAdapter(layoutInflater)
        getMapAsync {
            map = it
            it.setInfoWindowAdapter(infoWindowAdapter)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    LatLng(MINSK_CENTER_LATITUDE, MINSK_CENTER_LONGITUDE),
                    MAP_ZOOM))
            it.setOnInfoWindowClickListener {
                val flat = infoWindowAdapter?.getFlat(it)
                if(flat != null) {
                    val intent = FlatDetailsActivity.getCallingIntent(context, flat)
                    startActivity(intent)
                }
            }
        }
    }

    fun setFlats(flats: List<Flat>?) {
        map?.clear()

        if (flats == null)
            return

        for (flat in flats) {
            val lat = flat.getLatitude()
            val long = flat.getLongitude()
            if (lat != null && long != null) {
                val flatsMarker = map?.addMarker(
                        MarkerOptions().position(LatLng(lat, long)))

                if (flatsMarker != null) {
                    infoWindowAdapter?.addFlat(flatsMarker, flat)
                }
            }
        }
    }
}