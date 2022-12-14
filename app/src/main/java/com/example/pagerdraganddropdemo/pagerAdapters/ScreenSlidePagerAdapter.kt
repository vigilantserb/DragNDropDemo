package com.example.pagerdraganddropdemo.pagerAdapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pagerdraganddropdemo.GridFragment
import com.example.pagerdraganddropdemo.adapters.Item

class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa), DragNDropCallback {

    private val fragmentsMap = HashMap<Int, GridFragment>()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = GridFragment.newInstance(position).apply {
            attachDragNDropCallback(this@ScreenSlidePagerAdapter)
        }
        fragmentsMap[position] = fragment
        return fragment
    }

    override fun onDropEvent(from: Item, to: Item) {
        Log.i("BOBAN", "HIGH ---- Drop ${from.parentIndex} ${to.parentIndex}")
        if (from.parentIndex == to.parentIndex) {
            fragmentsMap[to.parentIndex]?.swapItems(from, to)
        } else {
            val fromParentFragment = fragmentsMap[from.parentIndex]!!
            val toParentFragment = fragmentsMap[to.parentIndex]!!

            val fromParentItems = fromParentFragment.adapter.items
            val toParentItems = toParentFragment.adapter.items

            val indexInFrom = fromParentItems.indexOf(from)
            val indexInTo = toParentItems.indexOf(to)

            fromParentItems[indexInFrom] = to.copy(parentIndex = from.parentIndex)
            toParentItems[indexInTo] = from.copy(parentIndex = to.parentIndex)

            fromParentFragment.adapter.notifyItemChanged(indexInFrom)
            toParentFragment.adapter.notifyItemChanged(indexInTo)
        }
        Log.i("BOBAN", "FROM: $from to: $to")
    }
}