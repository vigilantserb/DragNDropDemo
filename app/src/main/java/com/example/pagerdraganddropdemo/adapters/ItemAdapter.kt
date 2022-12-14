package com.example.pagerdraganddropdemo.adapters

import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.DragStartHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pagerdraganddropdemo.MainActivity
import com.example.pagerdraganddropdemo.R
import com.example.pagerdraganddropdemo.databinding.ItemGridBinding
import java.util.Collections

class ItemAdapter(private val activity: MainActivity, indexOfFragment: Int) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    lateinit var dropCallback: ((Item, Item) -> Unit)

    internal val items = mutableListOf(
        Item(1, indexOfFragment, "1"),
        Item(2, indexOfFragment, "2"),
        Item(3, indexOfFragment, ""),
        Item(4, indexOfFragment, ""),
        Item(5, indexOfFragment, ""),
        Item(6, indexOfFragment, "6"),
        Item(7, indexOfFragment, "7"),
        Item(8, indexOfFragment, ""),
        Item(9, indexOfFragment, "9")
    )

    inner class ItemViewHolder(private val binding: ItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var boundItem: Item

        init {

            binding.card.setOnDragListener { v, event ->
                val eventItem = event.localState as Item // onaj koji se dropuje
                // boundItem je onaj koji je vec na mestu
                when (event.action) {
                    DragEvent.ACTION_DROP -> {
                        dropCallback.invoke(eventItem, boundItem)
                        Log.e("BOBAN", "DROP::: boundItem: $boundItem eventItem: $eventItem")
                        true
                    }
                    else -> {
                        true
                    }
                }
            }
        }

        fun bind(item: Item) {
            boundItem = item
            if (item.isEmpty) {
                binding.text.text = item.name
                binding.card.setBackgroundColor(activity.getColor(R.color.transparent))
                binding.textDropTarget.visibility = View.VISIBLE
            } else {
                binding.card.setBackgroundColor(activity.getColor(R.color.black))
                binding.textDropTarget.visibility = View.GONE
                binding.text.text = item.name

                DragStartHelper(binding.card) { view, _ ->
                    val clipDataItem = ClipData.Item("tekst")
                    val dataToDrag = ClipData(
                        "tekst",
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        clipDataItem
                    )

                    view.startDragAndDrop(
                        dataToDrag,
                        View.DragShadowBuilder(view),
                        boundItem,
                        View.DRAG_FLAG_GLOBAL
                    )

                }.attach()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding = ItemGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
//        DropHelper.configureView(
//            activity,
//            itemBinding.textDropTarget,
//            arrayOf(
//                ClipDescription.MIMETYPE_TEXT_PLAIN,
//                "image/*",
//                "application/x-arc-uri-list" // Support external items on Chrome OS Android 9
//            ),
//            DropHelper.Options.Builder()
//                .addInnerEditTexts()
//                .setHighlightColor(activity.getColor(R.color.colorAccent))
//                // Match the radius of the view's background drawable
//                .setHighlightCornerRadiusPx(activity.resources.getDimensionPixelSize(R.dimen.drop_target_corner_radius))
//                .build()
//        ) { _, payload ->
//            Log.i("BOBAN", "HELLO WORLD")
//
//            // For the purposes of this demo, only handle the first ClipData.Item
//            val item = payload.clip.getItemAt(0)
//            val (_, remaining) = payload.partition { it == item }
//
//            // Allow the system to handle any remaining ClipData.Item objects if applicable
//            remaining
//        }

        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        items.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = items.size
    fun swapItems(from: Item, to: Item) {
        Collections.swap(items, items.indexOf(from), items.indexOf(to))
        notifyDataSetChanged()
    }
}

data class Item(
    val id: Int,
    var parentIndex: Int,
    val name: String,
    val isEmpty: Boolean = name.isEmpty()
)