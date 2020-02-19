package com.sound.rem.ui.fragment.description_frag


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.sound.rem.R

import com.sound.rem.R.*
import com.sound.rem.models.PictureProp
import com.sound.rem.models.Property
import com.sound.rem.ui.util.MapFragment
import com.sound.rem.ui.util.SliderAdapter
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_description.*


class DescriptionFragment : Fragment() {

    private lateinit var dbViewModel : REM_Database_ViewModel
    private lateinit var mapFrag:MapFragment
    private lateinit var pict:Disposable

    companion object{
        fun newInstance(): DescriptionFragment =
            DescriptionFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(layout.fragment_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)
        mapFrag = MapFragment.newInstance(dbViewModel)
        childFragmentManager.beginTransaction().replace(R.id.mapContainerDesc,mapFrag).commit()
        getPropertyData()
        super.onViewCreated(view, savedInstanceState)
    }

    fun getPropertyData(){
        dbViewModel.actualProperty.observe(viewLifecycleOwner, Observer { property ->
            updateUi(property)
            if (property.lat != null && property.lng != null)
                mapFrag.dataChange(LatLng(property.lat!!,property.lng!!))
            getPictures(property)
        })
    }

    private fun getPictures(property: Property) {
        pict = dbViewModel.getAllPics(property.idProperty!!)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                updateSlider(it)
            }
    }

    fun updateUi(property: Property){
        tvType_desc.text = property.propertyKind
        tvDivInfo_desc.text = (property.idProperty.toString())
        tvPrice_desc.text = property.price.toString()
        tvPricePerM_desc.text = (property.price/property.surface).toString()
        tvCity_desc.text = property.adresse
    }

    fun updateSlider(photoList: List<PictureProp>){
        val adapter = SliderAdapter(
            requireContext(),
            photoList
        )
        viewPager.adapter = adapter
    }
}
