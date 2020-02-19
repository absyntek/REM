package com.sound.rem.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.sound.rem.R
import com.sound.rem.ui.fragment.description_frag.DescriptionFragment
import com.sound.rem.ui.fragment.property_list_frag.PropertyFragment
import kotlinx.android.synthetic.main.fragment_main.*

class PropertyBrowser : Fragment() {

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
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showFragment()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun showFragment (){
        childFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_list, PropertyFragment.newInstance())
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
            R.id.actionSearchP -> print("ok")
        }
        return super.onOptionsItemSelected(item)
    }
}