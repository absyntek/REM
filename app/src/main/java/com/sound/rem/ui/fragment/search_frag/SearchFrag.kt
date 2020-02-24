package com.sound.rem.ui.fragment.search_frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.chip.Chip
import com.sound.rem.R
import com.sound.rem.models.Property
import com.sound.rem.ui.ui_utils.DropDownAdapter
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.search_fragment.*

class SearchFrag : DialogFragment(), AdapterView.OnItemClickListener, View.OnClickListener {

    private lateinit var dbViewModel: REM_Database_ViewModel
    private lateinit var adapterAdresse: DropDownAdapter
    private var adresse = emptyList<AutocompletePrediction>()
    private var listProperty:List<Property>? = null
    private lateinit var mPlace: PlacesClient
    private var search:Disposable? = null

    private var cityes:List<String>? = null
    private var surfMin:Int? = null
    private var surfMax:Int? = null
    private var nbrRoom:Int? = null
    private var nbrBed:Int? = null
    private var priceMax:Long? = null
    private var priceMin:Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.search_fragment,container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)
        mPlace = Places.createClient(requireContext())
        onValid()
        setDropDown()
        setEdtListener()
        setupListenerAdresse()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setEdtListener() {
        edtMinSurface.doOnTextChanged { text, _, _, _ ->
            surfMin = if (text.isNullOrBlank()) null else text.toString().toInt()
            searchInBackGround() }
        edtMaxSurface.doOnTextChanged { text, _, _, _ ->
            surfMax = if (text.isNullOrBlank()) null else text.toString().toInt()
            searchInBackGround() }
        edtNbrRoomSearch.doOnTextChanged { text, _, _, _ ->
            nbrRoom = if (text.isNullOrBlank()) null else text.toString().toInt()
            searchInBackGround() }
        edtPriceMinSearch.doOnTextChanged { text, _, _, _ ->
            priceMin = if (text.isNullOrBlank()) null else text.toString().toLong()
            searchInBackGround() }
        edtPriceMaxSearch.doOnTextChanged { text, _, _, _ ->
            priceMax = if (text.isNullOrBlank()) null else text.toString().toLong()
            searchInBackGround() }
    }

    private fun setDropDown(){
        val tmp = emptyList<String>()
        adapterAdresse =
            DropDownAdapter(requireContext(), R.layout.dropdown_menu_popup_item, tmp, adresse)
        edtCitySearch.onItemClickListener = this
        edtCitySearch.setAdapter(adapterAdresse)
    }

    private fun setupListenerAdresse() {
        edtCitySearch.doOnTextChanged { text, _, _, _ ->
            val token = AutocompleteSessionToken.newInstance()

            val request = FindAutocompletePredictionsRequest.builder()
                .setCountry("FR")
                .setTypeFilter(TypeFilter.CITIES)
                .setSessionToken(token)
                .setQuery(text.toString())
                .build()

            mPlace.findAutocompletePredictions(request).addOnSuccessListener {
                adresse = it.autocompletePredictions
                adapterAdresse.updateData(adresse)
            }
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        edtCitySearch.text.clear()
        val splitedAdress = adresse[p2].getFullText(null).toString().split(",")
        val chip = Chip(requireContext())
        chip.setText(splitedAdress[0])
        chip.isCloseIconVisible = true
        chip.isCheckable = false
        chip.isClickable = false
        chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_city_black_24dp)
        chip.setOnCloseIconClickListener(this)
        chipGroupCity.addView(chip)
    }

    override fun onClick(view: View?) {
        val chip = view!! as Chip
        chipGroupCity.removeView(chip)
    }

    private fun searchInBackGround(){
        val query = UtilSearch.generateQuery(cityes,surfMin,surfMax,nbrRoom,nbrBed,priceMin,priceMax)
        search = dbViewModel.searchProperty(query)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess{
                listProperty = it
                val sizeList = it.size
                textNbrResult.text = "there is $sizeList result"
                btnSearchForResult.text = "Show Result ($sizeList)"
            }.subscribe()
    }

    private fun onValid(){
        btnSearchForResult.setOnClickListener {
            if (!listProperty.isNullOrEmpty()){
                dbViewModel.searchResultProperty.onNext(listProperty!!)
            }
        }
    }

    override fun onDestroy() {
        if (search != null){
            search!!.dispose()
        }
        super.onDestroy()
    }
}
