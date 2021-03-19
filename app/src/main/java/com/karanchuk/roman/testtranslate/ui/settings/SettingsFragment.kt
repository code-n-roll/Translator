package com.karanchuk.roman.testtranslate.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.karanchuk.roman.testtranslate.R

/**
 * Created by roman on 8.4.17.
 */

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//         initActionBar();
    }

    fun initActionBar() {
        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false)
            actionBar.elevation = 0f
            actionBar.setDisplayShowCustomEnabled(false)
            actionBar.title = resources.getString(R.string.title_settings)
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_TITLE
            actionBar.show()
        }
    }
}
