package com.xeinebiu.views.changelog.adapters

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.xeinebiu.views.changelog.R
import com.xeinebiu.views.changelog.models.ReleaseNote
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs

class ReleaseNoteViewHolder(
    itemView: View,
    private val requireNoteView: (ViewGroup) -> View,
    private val recycleNoteView: (View) -> Unit
) : RecyclerView.ViewHolder(itemView) {

    private val notesViews: Queue<View> = LinkedList()

    private val releaseTitleView: AppCompatTextView =
        itemView.findViewById(R.id.layout_tv_release_title)

    var releaseNote: ReleaseNote? = null
        set(value) {
            field = value

            releaseTitleView.text = value?.title.orEmpty()

            val viewGroup = itemView as ViewGroup
            val notes = value?.notes.orEmpty()
            val diff = notesViews.size - notes.size

            if (diff > 0) {
                for (i in 0 until diff) {
                    val view = notesViews.remove()
                    viewGroup.removeView(view)
                    recycleNoteView(view)
                }
            } else if (diff < 0) {
                val absDiff = abs(diff)
                for (i in 0 until absDiff) {
                    val view = requireNoteView(viewGroup)
                    notesViews.add(view)
                    viewGroup.addView(view, viewGroup.childCount - 1)
                }
            }

            notesViews.forEachIndexed { index, view ->
                view.findViewById<AppCompatTextView>(
                    R.id.layout_tv_release_note_title
                ).text = notes[index]
            }
        }
}
