package com.sound.rem.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.sound.rem.R
import com.sound.rem.models.Property
import com.sound.rem.ui.fragment.description_frag.DescriptionFragment
import com.sound.rem.ui.fragment.new_property_frag.NewPropertyFragment
import com.sound.rem.ui.fragment.property_list_frag.PropertyFragment
import com.sound.rem.ui.fragment.property_list_frag.PropertyListAdapter
import com.sound.rem.viewmodel.REM_Database_ViewModel
import kotlinx.android.synthetic.main.fragment_main.*

class PropertyBrowser : Fragment(), PropertyListAdapter.OnItemClickListener{

    private lateinit var dbViewModel : REM_Database_ViewModel

    val options = navOptions {
        anim {
            enter = R.anim.slide_in_top
            exit = R.anim.slide_out_bottom
            popEnter = R.anim.slide_in_left
            popExit = R.anim.slide_out_right
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        dbViewModel = ViewModelProvider(activity!!).get(REM_Database_ViewModel::class.java)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        showFragment()
        super.onResume()
    }

    private fun showFragment (){
        dbViewModel.listener = this
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.front, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.actionAddNewP -> findNavController().navigate(R.id.nav_newProperty, null, options)
            R.id.actionSearchP -> print("ok")//TODO
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(item: Property) {
        dbViewModel.actualProperty.onNext(item)
        if (frame_layout_detail == null){
            findNavController().navigate(R.id.action_nav_home_to_descriptionFragment, null, options)
        }
    }
}