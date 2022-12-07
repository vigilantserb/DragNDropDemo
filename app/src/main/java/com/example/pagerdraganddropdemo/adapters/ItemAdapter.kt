package com.example.pagerdraganddropdemo.adapters

import android.content.ClipData
import android.content.ClipDescription
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.DragStartHelper
import androidx.draganddrop.DropHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pagerdraganddropdemo.MainActivity
import com.example.pagerdraganddropdemo.R
import com.example.pagerdraganddropdemo.databinding.ItemGridBinding
import java.io.FileInputStream
import java.io.FileNotFoundException


class ItemAdapter(private val activity: MainActivity) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val items: List<Item> = listOf(
        Item(1, "TEKST TEKST 1", false),
        Item(3, "TEKST TEKST 2", false)
    )

    inner class ItemViewHolder(private val binding: ItemGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnDragListener { v, event ->
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                        Log.i("BOBAN", "STARTED V X:${v.x} Y:${v.y} event X${event.x} Y${event.y}")
                    }
                    DragEvent.ACTION_DRAG_LOCATION -> {
                        Log.i("BOBAN", "LOCATION V X:${v.x} Y:${v.y} event X${event.x} Y${event.y}")
                    }
                    DragEvent.ACTION_DROP -> {
                        Log.e("BOBAN", "DROP V X:${v.x} Y:${v.y} event X${event.x} Y${event.y}")
                    }
                    DragEvent.ACTION_DRAG_ENDED -> {
                        Log.e("BOBAN", "ENDED V X:${v.x} Y:${v.y} event X${event.x} Y${event.y}")
                    }
                    else -> {

                    }
                }
                if (event.x > MainActivity.width - 100) {
                    Toast.makeText(activity, "TOAST", Toast.LENGTH_SHORT).show()
                }
                true
            }
            // Use the DragStartHelper class to easily support initiating drag and drop in response to
            // both long press and mouse drag events. Note the call to attach() at the end. Without it,
            // the listener would never actually be attached to the view. Also note that attach() replaces
            // any OnTouchListener or OnLongClickListener already attached to the view.
            DragStartHelper(binding.root) { view, _ ->
                val text = (view as TextView).text

                // Create the ClipData to be shared
                val dragClipData = ClipData.newPlainText(/*label*/"Text", text)

                // Use the default drag shadow
                val dragShadowBuilder = View.DragShadowBuilder(view)

                // Initiate the drag. Note the DRAG_FLAG_GLOBAL, which allows for drag events to be listened
                // to by apps other than the source app.
                view.startDragAndDrop(
                    dragClipData,
                    dragShadowBuilder,
                    null,
                    View.DRAG_FLAG_GLOBAL
                )
            }.attach()

            DropHelper.configureView(
                activity,
                binding.root,
                arrayOf(
                    ClipDescription.MIMETYPE_TEXT_PLAIN,
                    "image/*",
                    "application/x-arc-uri-list" // Support external items on Chrome OS Android 9
                ),
                DropHelper.Options.Builder()
                    .setHighlightColor(activity.getColor(R.color.purple_200))
                    // Match the radius of the view's background drawable
                    .setHighlightCornerRadiusPx(activity.resources.getDimensionPixelSize(R.dimen.drop_target_corner_radius))
                    .build()
            ) { _, payload ->
                resetDropTarget()

                // For the purposes of this demo, only handle the first ClipData.Item
                val item = payload.clip.getItemAt(0)
                val (_, remaining) = payload.partition { it == item }

                when {
                    payload.clip.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ->
                        handlePlainTextDrop(item)
                }

                // Allow the system to handle any remaining ClipData.Item objects if applicable
                remaining
            }
        }

        private fun handlePlainTextDrop(item: ClipData.Item) {
            // The text is contained in the ClipData.Item
            if (item.text != null) {
                binding.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
                binding.text.text = activity.getString(
                    R.string.drop_text,
                    item.text.substring(0, item.text.length.coerceAtMost(200))
                )
            } else {
                // The text is in a file pointed to by the ClipData.Item
                val parcelFileDescriptor: ParcelFileDescriptor? = try {
                    activity.contentResolver.openFileDescriptor(item.uri, "r")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    Log.e("BOBAN", "FileNotFound")
                    return
                }

                if (parcelFileDescriptor == null) {
                    Log.e("BOBAN", "Could not load file")
                    binding.text.text =
                        activity.resources.getString(R.string.drop_error, item.uri.toString())
                } else {
                    val fileDescriptor = parcelFileDescriptor.fileDescriptor
                    val bytes = ByteArray(200)

                    try {
                        FileInputStream(fileDescriptor).use {
                            it.read(bytes, 0, 200)
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e("BOBAN", "Unable to read file: ${e.message}")
                    }

                    binding.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                    binding.text.text = activity.getString(R.string.drop_text, String(bytes))
                }
            }
        }

        private fun resetDropTarget() {
            binding.text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.text.background =
                ContextCompat.getDrawable(activity, R.drawable.bg_target_normal)
        }

        fun bind(item: Item) {
            binding.text.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemGridBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        items.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = 9

}

data class Item(val position: Int, val name: String, val isEmpty: Boolean = true)