package com.example.pagerdraganddropdemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pagerdraganddropdemo.adapters.ItemAdapter
import com.example.pagerdraganddropdemo.databinding.FragmentFirstBinding
import java.util.Timer
import java.util.TimerTask

class GridFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.grid.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
        binding.grid.adapter = ItemAdapter(requireActivity() as MainActivity)
        binding.dropRight.setOnDragListener(getDragListenerWithNextPosition(+1))
        binding.dropLeft.setOnDragListener(getDragListenerWithNextPosition(-1))
        return binding.root
    }

    private fun getDragListenerWithNextPosition(nextIndex: Int) =
        View.OnDragListener { view, dragEvent ->
            var timer = Timer()
            val draggableItem = dragEvent.localState as View

            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    Log.i("BOBAN", "STARTED")
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    timer.cancel()
                    timer.purge()
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            Handler(Looper.getMainLooper()).post {
                                with((activity as MainActivity).binding.viewPager2) {
                                    setCurrentItem(currentItem + nextIndex, true)
                                }
                            }
                        }
                    }, 1500)
                    Log.i("BOBAN", "ENTERED")
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    Log.i("BOBAN", "LOCATION ${dragEvent.x}")
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    Log.i("BOBAN", "EXITED")
                    draggableItem.visibility = View.VISIBLE
                    view.invalidate()
                    timer.cancel()
                    timer.purge()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    Log.i("BOBAN", "DROP")
                    timer.cancel()
                    timer.purge()
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    Log.i("BOBAN", "ENDED")
                    draggableItem.visibility = View.VISIBLE
                    view.invalidate()
                    timer.cancel()
                    timer.purge()
                    true
                }
                else -> {
                    Log.e("BOBAN", "ELSE")
                    false
                }
            }
        }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): GridFragment {
            return GridFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}