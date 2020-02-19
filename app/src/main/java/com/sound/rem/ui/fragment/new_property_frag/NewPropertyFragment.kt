package com.sound.rem.ui.fragment.new_property_frag


import android.app.Activity.INPUT_METHOD_SERVICE
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.sound.rem.R
import com.sound.rem.models.PictureProp
import com.sound.rem.models.Property
import com.sound.rem.ui.util.DropDownAdapter
import com.sound.rem.ui.util.MapFragment
import com.sound.rem.ui.util.SliderAdapter
import com.sound.rem.utlis.Utils
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_new_property.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class NewPropertyFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var dbViewModel: REM_Database_ViewModel
    lateinit var currentPhotoPath: String
    lateinit var mPlace: PlacesClient
    val REQUEST_TAKE_PHOTO = 1
    var photoList: ArrayList<PictureProp> = arrayListOf()
    val error:String = "Required"
    lateinit var adapterAdresse:DropDownAdapter
    private var adresse = emptyList<AutocompletePrediction>()
    private lateinit var mapFrag:MapFragment
    private var latLng: LatLng? = null
    private lateinit var currencys:Array<String>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_property, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        dbViewModel = ViewModelProvider(this).get(REM_Database_ViewModel::class.java)
        Places.initialize(requireContext(), resources.getString(R.string.google_maps_key))
        mapFrag = MapFragment.newInstance(dbViewModel)
        childFragmentManager.beginTransaction().replace(R.id.mapFragNewProp, mapFrag).commit()
        mPlace = Places.createClient(requireContext())

        setDropdowns()
        setUpButtons()
        setupListenerAdresse()

        super.onViewCreated(view, savedInstanceState)
    }

    fun setupListenerAdresse(){
        edtAdresse.doOnTextChanged { text, _, _, _ ->
            val token = AutocompleteSessionToken.newInstance()

            val request = FindAutocompletePredictionsRequest.builder()
                .setCountry("FR")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(text.toString())
                .build()

            mPlace.findAutocompletePredictions(request).addOnSuccessListener {
                adresse = it.autocompletePredictions
                adapterAdresse.updateData(adresse)
            }
        }

    }

    private fun setDropdowns (){
        // DropDown for kind of property
        val propertyKind = requireContext().resources.getStringArray(R.array.property_kind)
        val adapterPropertyKind = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, propertyKind)
        dropDownKind.inputType = 0
        dropDownKind.setAdapter(adapterPropertyKind)

        // DropDown for currencys
        currencys = requireContext().resources.getStringArray(R.array.currencys)
        val adapterCurrencys = ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, currencys)
        edtCurrency.inputType = 0
        edtCurrency.setAdapter(adapterCurrencys)

        val tmp = emptyList<String>()
        adapterAdresse = DropDownAdapter(requireContext(), R.layout.dropdown_menu_popup_item, tmp, adresse)
        edtAdresse.onItemClickListener = this
        edtAdresse.setAdapter(adapterAdresse)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    Utils.createImageFile(requireContext()).apply { currentPhotoPath = absolutePath }
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.sound.rem.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            val isFirst = photoList.isEmpty()
            val tmpPhoto = PictureProp(currentPhotoPath,-1, isFirst)
            photoList.add(tmpPhoto)
            updateSlider(photoList)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateSlider(photoList: ArrayList<PictureProp>) {
        val adapter = SliderAdapter(requireContext(), photoList)
        viewPagerNewProp.adapter = adapter
    }

    fun setUpButtons(){
        buttonAddPic.setOnClickListener{
            dispatchTakePictureIntent()
        }

        btn_newProperty_Ok.setOnClickListener {
            if (verrifyConditions()){
                preparToSave()
            }
        }
    }

    fun verrifyConditions():Boolean{
        var isItOk = true
        if (dropDownKind.text.isEmpty()) { etKind.error=error ; isItOk = false} else  etKind.error=null
        if (edtSurface.text!!.isEmpty()) { etSurface.error=error ; isItOk = false } else etSurface.error=null
        if (edtPrice.text!!.isEmpty()) { etPrice.error=error ; isItOk = false }else etPrice.error=null
        if (edtCurrency.text!!.isEmpty()) { etCurrency.error=error ; isItOk = false }else etCurrency.error=null
        if (edtNbrRoom.text!!.isEmpty()) { etNbrRoom.error=error ; isItOk = false }else etNbrRoom.error=null
        if (edtNbrBedRoom.text!!.isEmpty()) { etNbrBedRoom.error=error ; isItOk = false }else etNbrBedRoom.error=null
        if (edtAdresse.text!!.isEmpty()) { etAdresse.error=error ; isItOk = false }else etAdresse.error=null
        if (edtCity.text!!.isEmpty()) { etCity.error=error ; isItOk = false }else etCity.error=null
        if (edtCountry.text!!.isEmpty()) { etCountry.error=error ; isItOk = false }else etCountry.error=null
        return isItOk
    }

    fun preparToSave(){
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())

        val lat:Double? ;val lng:Double?
        if (latLng !=null){ lat = latLng!!.latitude ; lng = latLng!!.longitude }
        else{ lat = null ; lng = null }

        val property = Property(
            null,
            dropDownKind.text.toString(),
            edtDescription.text.toString(),
            edtAdresse.text.toString(),
            edtCity.text.toString(),
            edtCountry.text.toString(),
            lat,
            lng,
            edtPrice.text.toString().toInt(),
            edtCurrency.text.toString().equals(currencys[0]),
            edtSurface.text.toString().toInt(),
            edtNbrRoom.text.toString().toInt(),
            timeStamp,
            null
        )
        val tmp = dbViewModel.addProperty(property).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                setPicturs(it)
            }
    }

    fun setPicturs(propertyId: Long){
        photoList.forEach {it.idProperty = propertyId}
        dbViewModel.addPhotos(photoList)
        findNavController().navigate(R.id.next_action)
    }


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        edtAdresse.text = Editable.Factory.getInstance().newEditable(adresse.get(p2).getPrimaryText(null))
        val cityCountry = adresse.get(p2).getSecondaryText(null).toString().split(",")
        edtCity.text = Editable.Factory.getInstance().newEditable(cityCountry.get(0))
        edtCountry.text = Editable.Factory.getInstance().newEditable(cityCountry.get(1))


        val placeFields = arrayListOf(Place.Field.ID, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(adresse.get(p2).placeId, placeFields)

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(edtAdresse.windowToken,0)

        mPlace.fetchPlace(request).addOnSuccessListener { response ->
            latLng = response.place.latLng
            mapFrag.dataChange(latLng)
        }
    }

    override fun onStop() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(null,0)
        super.onStop()
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}
