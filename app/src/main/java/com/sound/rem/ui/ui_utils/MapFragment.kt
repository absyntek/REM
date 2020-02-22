package com.sound.rem.ui.ui_utils


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sound.rem.R
import com.sound.rem.viewmodel.REM_Database_ViewModel


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var dbViewModel : REM_Database_ViewModel
    private var latLng: List<LatLng?>? = null
    private var mMap: GoogleMap? = null

    companion object {
        @JvmStatic
        fun newInstance(dbViewModel : REM_Database_ViewModel) = MapFragment().apply {
            this.dbViewModel = dbViewModel
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null){
            val options = GoogleMapOptions().liteMode(true)
            mapFragment = SupportMapFragment.newInstance(options)
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
            mapFragment.getMapAsync(this)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.mMap = map
        addMarkers()
    }

    fun dataChange(latLng: List<LatLng?>){
        this.latLng = latLng
        if (mMap != null){
            addMarkers()
        }
    }

    private fun addMarkers(){
        if (!this.latLng.isNullOrEmpty()) {
            mMap!!.clear()
            latLng!!.forEach {
                if (it != null){
                    mMap!!.addMarker(MarkerOptions().position(it).title("Here"))
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(it))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomIn())
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null)
                    //TODO si la lista à plus de 1 entré alors zoom au milieu
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (mMap == null){
            SupportMapFragment.newInstance().getMapAsync(this)
        }
    }
}
