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
import com.example.pagerdraganddropdemo.adapters.Item
import com.example.pagerdraganddropdemo.adapters.ItemAdapter
import com.example.pagerdraganddropdemo.databinding.FragmentFirstBinding
import com.example.pagerdraganddropdemo.pagerAdapters.DragNDropCallback
import java.util.*


class GridFragment : Fragment() {
    var indexOfFragment: Int = -1
    private lateinit var binding: FragmentFirstBinding

    lateinit var adapter: ItemAdapter
    private lateinit var dragNDropCallback: DragNDropCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        indexOfFragment = arguments?.getInt(ARG_SECTION_NUMBER)!!
        Log.e("BOBAN", "INDEX: $indexOfFragment")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        setUpGrid()
        binding.dropRight.setOnDragListener(getDragListenerWithNextPosition(+1))
        binding.dropLeft.setOnDragListener(getDragListenerWithNextPosition(-1))
        return binding.root
    }

    private fun setUpGrid() {
        adapter = ItemAdapter(requireActivity() as MainActivity, indexOfFragment).apply {
            dropCallback = { from, to ->
                dragNDropCallback.onDropEvent(from, to)
            }
        }
        binding.grid.adapter = adapter
        binding.grid.layoutManager =
            GridLayoutManager(requireContext(), 3, GridLayoutManager.VERTICAL, false)
    }

    private fun getDragListenerWithNextPosition(nextIndex: Int) =
        View.OnDragListener { view, dragEvent ->
            var timer = Timer()

            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
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
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    view.invalidate()
                    timer.cancel()
                    timer.purge()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    timer.cancel()
                    timer.purge()
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
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

    fun attachDragNDropCallback(dragNDropCallback: DragNDropCallback) {
        this.dragNDropCallback = dragNDropCallback
    }

    fun swapItems(from: Item, to: Item) {
        adapter.swapItems(from, to)
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