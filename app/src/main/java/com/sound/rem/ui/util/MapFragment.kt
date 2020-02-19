package com.sound.rem.ui.util


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sound.rem.R
import com.sound.rem.models.Property
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var property: Property
    private lateinit var dbViewModel : REM_Database_ViewModel
    private lateinit var disposable: Disposable
    private var latLng: LatLng? = null

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
        addMarker()
    }

    fun dataChange(latLng: LatLng?){
        //TODO Change by list of Property
        this.latLng = latLng
        if (mMap != null){
            addMarker()
        }
    }

    fun addMarker(){
        if (this.latLng != null) {
            mMap!!.clear()
            mMap!!.addMarker(MarkerOptions().position(this.latLng!!).title("Here"))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap!!.animateCamera(CameraUpdateFactory.zoomIn())
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mMap == null){
            SupportMapFragment.newInstance().getMapAsync(this)
        }
    }
}
