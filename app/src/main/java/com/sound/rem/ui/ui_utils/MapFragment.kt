package com.sound.rem.ui.ui_utils


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sound.rem.R
import com.sound.rem.models.Property
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var dbViewModel : REM_Database_ViewModel
    private lateinit var actualProp: Disposable
    private lateinit var onMapItemClickListener: OnMapItemClickListener

    private var propertyList: List<Property>? = null
    private var mMap: GoogleMap? = null
    private var latLng: LatLng? = null

    companion object {
        @JvmStatic
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        if (mapFragment == null){
            val options = GoogleMapOptions().liteMode(dbViewModel.mapLite)
            mapFragment = SupportMapFragment.newInstance(options)
            childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()
            mapFragment.getMapAsync(this)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.mMap = map
        selectKindMap()
    }

    fun dataChangeSimpleMap(latLng: LatLng?){
        this.propertyList = null
        this.latLng = latLng
        if (mMap != null) selectKindMap()
    }

    fun dataChangeMultiple(propertyList: List<Property>){
        actualProp = dbViewModel.actualProperty
            .skipLast(1)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext{
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.lat!!,it.lng!!)))
                mMap!!.animateCamera(CameraUpdateFactory.zoomIn())
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null)
            }.subscribe()
        this.latLng = null
        this.propertyList = propertyList
        if (mMap != null) selectKindMap()
    }

    private fun selectKindMap(){
        mMap!!.clear()

        if (this.latLng != null) {
            mMap!!.addMarker(MarkerOptions().position(latLng!!).title("Here"))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap!!.animateCamera(CameraUpdateFactory.zoomIn())
            mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10F), 2000, null)
        }else if (!this.propertyList.isNullOrEmpty()){

            mMap!!.setOnInfoWindowClickListener { marker ->
                dbViewModel.getPropertyById(marker.tag as Long)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess{
                        actualProp.dispose()
                        dbViewModel.actualProperty.onNext(it)
                        onMapItemClickListener.onMapClick(it)
                    }.subscribe()
            }

            val bounds = LatLngBounds.builder()
            this.propertyList!!.forEach {
                val tmpLatLng = LatLng(it.lat!!,it.lng!!)
                bounds.include(tmpLatLng)
                val tmpMarker = mMap!!.addMarker(MarkerOptions().position(tmpLatLng).title(it.propertyKind))
                tmpMarker.tag = it.idProperty
            }
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),100))
        }
    }

    override fun onResume() {
        super.onResume()
        if (mMap == null){
            SupportMapFragment.newInstance().getMapAsync(this)
        }
    }

    fun setCallBack (onMapItemClickListener: OnMapItemClickListener){
        this.onMapItemClickListener = onMapItemClickListener
    }

    interface OnMapItemClickListener {
        fun onMapClick(item: Property)
    }
}