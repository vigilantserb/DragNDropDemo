package com.example.pagerdraganddropdemo

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.pagerdraganddropdemo.pagerAdapters.ScreenSlidePagerAdapter
import com.example.pagerdraganddropdemo.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ScreenSlidePagerAdapter(this)
        with(binding) {
            viewPager2.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                Log.i("BOBAN", "TAB:$tab POSITION:$position")
            }.attach()
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
        width = displayMetrics.widthPixels
        Log.i("BOBAN", "H: $height W: $width")
    }

    companion object {
        var height: Int = 0
        var width: Int = 0
    }

}