package com.example.pagerdraganddropdemo

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.TypedValue
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.DragStartHelper
import androidx.draganddrop.DropHelper
import androidx.fragment.app.Fragment
import com.example.pagerdraganddropdemo.databinding.SecondFragmentBinding
import java.io.FileInputStream
import java.io.FileNotFoundException

class SecondFragment : Fragment() {

    lateinit var binding: SecondFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = SecondFragmentBinding.inflate(inflater, container, false)
        initDragAndDrop(binding)
        binding.root.setOnDragListener(maskDragListener)
        return binding.root
    }

    private fun initDragAndDrop(binding: SecondFragmentBinding) {
        DragStartHelper(binding.draggable) { view, _ ->
            val text = (view as TextView).text

            val item = ClipData.Item(text)

            val dataToDrag = ClipData(
                text,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            val maskShadow = MaskDragShadowBuilder(view)

            view.startDragAndDrop(
                dataToDrag,
                maskShadow,
                view,
                View.DRAG_FLAG_GLOBAL
            )

        }.attach()

        DropHelper.configureView(
            requireActivity(),
            binding.drop,
            arrayOf(
                ClipDescription.MIMETYPE_TEXT_PLAIN,
                "image/*",
                "application/x-arc-uri-list" // Support external items on Chrome OS Android 9
            ),
            DropHelper.Options.Builder()
                .setHighlightColor(activity!!.getColor(R.color.purple_200))
                // Match the radius of the view's background drawable
                .setHighlightCornerRadiusPx(activity!!.resources.getDimensionPixelSize(R.dimen.drop_target_corner_radius))
                .build()
        ) { _, payload ->
            resetDropTarget(binding)

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

    private fun resetDropTarget(binding: SecondFragmentBinding) {
        binding.drop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
        binding.drop.background =
            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_target_normal)
    }

    private fun handlePlainTextDrop(item: ClipData.Item) {
        // The text is contained in the ClipData.Item
        if (item.text != null) {
            binding.drop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            binding.drop.text = activity!!.getString(
                R.string.drop_text,
                item.text.substring(0, item.text.length.coerceAtMost(200))
            )
        } else {
            // The text is in a file pointed to by the ClipData.Item
            val parcelFileDescriptor: ParcelFileDescriptor? = try {
                activity!!.contentResolver.openFileDescriptor(item.uri, "r")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                Log.e("BOBAN", "FileNotFound")
                return
            }

            if (parcelFileDescriptor == null) {
                Log.e("BOBAN", "Could not load file")
                binding.drop.text =
                    activity!!.resources.getString(R.string.drop_error, item.uri.toString())
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

                binding.drop.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                binding.drop.text = activity!!.getString(R.string.drop_text, String(bytes))
            }
        }
    }

    private val maskDragListener = View.OnDragListener { view, dragEvent ->
        val draggableItem = dragEvent.localState as View
        Log.i("BOBAN", "X: ${dragEvent.x}")

        if (dragEvent.x > 1000) {
            (requireActivity() as MainActivity).binding.viewPager2.setCurrentItem(2, true)
        }

        when (dragEvent.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            DragEvent.ACTION_DROP -> {
                binding.drop.alpha = 1.0f
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggableItem.visibility = View.VISIBLE
                view.invalidate()
                true
            }
            else -> {
                false
            }
        }
    }
}

private class MaskDragShadowBuilder(view: View) : View.DragShadowBuilder(view) {

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Sets the width of the shadow to full width of the original View
        val width: Int = view.width

        // Sets the height of the shadow to full height of the original View
        val height: Int = view.height

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height)

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the Drawable in the Canvas passed in from the system.
        super.onDrawShadow(canvas)
    }
}

