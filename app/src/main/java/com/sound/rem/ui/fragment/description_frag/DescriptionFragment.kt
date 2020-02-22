package com.sound.rem.ui.fragment.description_frag


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.sound.rem.R

import com.sound.rem.R.*
import com.sound.rem.models.PictureProp
import com.sound.rem.models.Property
import com.sound.rem.ui.ui_utils.MapFragment
import com.sound.rem.ui.ui_utils.SliderAdapter
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_description.*


class DescriptionFragment : Fragment() {

    private lateinit var dbViewModel : REM_Database_ViewModel
    private lateinit var mapFrag:MapFragment
    private lateinit var pict:Disposable
    private lateinit var property: Property

    private val compositeDisposable = CompositeDisposable()

    companion object{ fun newInstance() = DescriptionFragment() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return inflater.inflate(layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)
        mapFrag = MapFragment.newInstance(dbViewModel)
        childFragmentManager.beginTransaction().replace(R.id.mapContainerDesc,mapFrag).commit()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        dbViewModel.actualProperty
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnNext {property ->
                if (property != null){
                    this.property = property
                    updateUi()
                    if (property.lat != null && property.lng != null)
                        mapFrag.dataChange(listOf(LatLng(property.lat!!,property.lng!!)))
                    getPictures(property)
                }
            }
            .doOnError { Log.e(this.toString(),it.message!!)}
            .subscribe().addTo(compositeDisposable)
        super.onResume()
    }

    private fun getPictures(property: Property) {
        pict = dbViewModel.getAllPics(property.idProperty!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .doOnSuccess {updateSlider(it)}
            .doOnError { Log.e(this.toString(), it.message!!) }
            .subscribe().addTo(compositeDisposable)
    }

    private fun updateUi(){
        tvType_desc.text = property.propertyKind
        tvDivInfo_desc.text = (property.idProperty.toString())
        tvPrice_desc.text = property.price.toString()
        tvPricePerM_desc.text = (property.price/property.surface).toString()
        tvCity_desc.text = property.adresse
    }

    private fun updateSlider(photoList: List<PictureProp>){
        val adapter = SliderAdapter(requireContext(), photoList)
        viewPager.adapter = adapter
    }

    override fun onPause() {
        compositeDisposable.dispose()
        super.onPause()
    }
}
