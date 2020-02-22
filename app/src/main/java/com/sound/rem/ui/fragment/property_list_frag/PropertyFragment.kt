package com.sound.rem.ui.fragment.property_list_frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.sound.rem.R
import com.sound.rem.models.Property
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers


class PropertyFragment : Fragment(){

    private lateinit var dbViewModel: REM_Database_ViewModel
    private lateinit var adapter: PropertyListAdapter
    private val compositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance() = PropertyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_property_list, container, false)

        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)
        setAdapter(view)
        return view
    }

    private fun setAdapter (view: View){
        // Set the adapter
        if (view is RecyclerView){
            adapter = PropertyListAdapter(requireContext(), dbViewModel.listener)
            view.adapter = adapter

            // if orientation screen is land so linear = horizontal
            when {
                resources.getBoolean(R.bool.isLand) -> {
                    view.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                }
                resources.getBoolean(R.bool.isTablet) -> {
                    view.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                }
                else -> {
                    view.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                }
            }
            adapter.setViewModel(dbViewModel)
        }
    }

    override fun onStart() {
        dbViewModel.allPropertys
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isNotEmpty()) {
                    dbViewModel.actualProperty.onNext(it[0])
                    adapter.setPropertys(it)
                }
            }.addTo(compositeDisposable)
        super.onStart()
    }

    override fun onStop() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
        super.onStop()
    }
}
