package com.sound.rem.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.gms.maps.model.LatLng
import com.sound.rem.R
import com.sound.rem.models.Property
import com.sound.rem.ui.fragment.description_frag.DescriptionFragment
import com.sound.rem.ui.fragment.property_list_frag.PropertyFragment
import com.sound.rem.ui.fragment.property_list_frag.PropertyListAdapter
import com.sound.rem.ui.fragment.search_frag.SearchFrag
import com.sound.rem.ui.ui_utils.MapFragment
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*

class PropertyBrowser : Fragment(), PropertyListAdapter.OnItemClickListener, MapFragment.OnMapItemClickListener{

    private lateinit var dbViewModel : REM_Database_ViewModel
    private var mapFragment: MapFragment? = null

    val options = navOptions {
        anim {
            enter = R.anim.slide_in_right
            exit = R.anim.slide_out_left
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)
        dbViewModel.listener = this

        // TODO add comment
        if(!dbViewModel.searchResultProperty.hasValue()){
            dbViewModel.allPropertys
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if (it.isNotEmpty()) {
                        dbViewModel.searchResultProperty.onNext(it)
                    }
                }
                .subscribe()
        }
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        showFragmentWithDesc()
        super.onResume()
    }

    private fun showFragmentWithDesc (){
        childFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_list,  PropertyFragment.newInstance())
            .commit()
        if (frame_layout_detail != null){
            childFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_detail,
                    DescriptionFragment.newInstance())
                .commit()
        }
    }

    private fun showFragmentWithMap (){
        mapFragment!!.setCallBack(this)
        if (frame_layout_detail != null){
            childFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_detail,
                    mapFragment!!)
                .commit()
        }else{
            childFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_list,  mapFragment!!)
                .commit()
        }

        dbViewModel.searchResultProperty.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext{
                mapFragment!!.dataChangeMultiple(it)
            }
            .subscribe()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.front, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.actionSearchP -> {
                val searchFragment = SearchFrag()
                searchFragment.show(childFragmentManager,"Search")
            }
            R.id.listToMap -> {
                if (mapFragment == null){
                    dbViewModel.mapLite = false
                    mapFragment = MapFragment.newInstance()
                    showFragmentWithMap()
                }else{
                    mapFragment = null
                    showFragmentWithDesc()
                }

            }
            R.id.switchCurrency -> {
                if (dbViewModel.isDollar.value!!) {
                    dbViewModel.isDollar.onNext(false)
                }else{
                    dbViewModel.isDollar.onNext(true)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(item: Property) {
        dbViewModel.actualProperty.onNext(item)
        if (frame_layout_detail == null){
            findNavController().navigate(R.id.action_nav_home_to_descriptionFragment, null, options)
        }
    }

    override fun onMapClick(item: Property) {
        if (frame_layout_detail == null){
            findNavController().navigate(R.id.action_nav_home_to_descriptionFragment, null, options)
        }else{
            childFragmentManager.beginTransaction()
                .replace(R.id.frame_layout_detail,
                    DescriptionFragment.newInstance())
                .commit()
        }
        mapFragment = null
    }
}