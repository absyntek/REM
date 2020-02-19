package com.sound.rem.ui.fragment.property_list_frag

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sound.rem.R
import com.sound.rem.viewmodel.REM_Database_ViewModel


class PropertyFragment : Fragment() {

    private lateinit var dbViewModel: REM_Database_ViewModel

    companion object {
        fun newInstance() = PropertyFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_property_list, container, false)

        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)

        // Set the adapter
        if (view is RecyclerView){
            val adapter = PropertyListAdapter(requireContext())
            view.adapter = adapter

            // if orientation screen is land and size 600 so linear = horizontal
            if (resources.getBoolean(R.bool.isTablet) && resources.getBoolean(R.bool.isLand)){
                view.layoutManager = LinearLayoutManager(requireContext())
            }else{
                view.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            }
            adapter.setViewModel(dbViewModel)
            dbViewModel.allPropertys.observe(this, Observer { propertys ->
                propertys?.let {
                    if (it.isNotEmpty()){
                        dbViewModel.actualProperty.value = it[0]
                    }
                    adapter.setPropertys(it)
                }
            })
        }
        return view
    }
}
