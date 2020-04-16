package com.xeinebiu.views.changelog.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.xeinebiu.views.changelog.R
import com.xeinebiu.views.changelog.models.ReleaseNote
import java.util.*
import kotlin.math.abs

class ReleaseNoteRvAdapter(
    private val context: Context,
    private val releaseNotes: List<ReleaseNote>,
    @LayoutRes private val releaseTitleLayoutId: Int,
    @LayoutRes private val releaseNoteLayoutId: Int,
    @LayoutRes private val releaseDividerLayoutId: Int
) :
    RecyclerView.Adapter<ReleaseNoteViewHolder>() {

    private val notesRecycleViewPool: Queue<View> = LinkedList()

    private val recycleNoteView: (View) -> Unit = {
        notesRecycleViewPool.add(it)
    }

    private val requireNoteView: (ViewGroup) -> View = { parent ->
        if (notesRecycleViewPool.isEmpty())
            layoutInflater.inflate(releaseNoteLayoutId, parent, false)
        else
            notesRecycleViewPool.remove()
    }

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    override fun getItemCount(): Int = releaseNotes.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReleaseNoteViewHolder {
        // create release container
        val releaseView = layoutInflater.inflate(R.layout.layout_release, parent, false) as ViewGroup

        // Release title
        val titleView = layoutInflater.inflate(releaseTitleLayoutId, parent, false)

        // Release divider
        val dividerView = layoutInflater.inflate(releaseDividerLayoutId, parent, false)

        releaseView.addView(titleView)
        releaseView.addView(dividerView)

        return ReleaseNoteViewHolder(releaseView, requireNoteView, recycleNoteView)
    }

    override fun onBindViewHolder(holder: ReleaseNoteViewHolder, position: Int) {
        val releaseNote = releaseNotes[position]
        holder.releaseNote = releaseNote
    }
}

class ReleaseNoteViewHolder(
    itemView: View,
    private val requireNoteView: (ViewGroup) -> View,
    private val recycleNoteView: (View) -> Unit
) :
    RecyclerView.ViewHolder(itemView) {

    private val notesViews: Queue<View> = LinkedList()
    private val releaseTitleView: AppCompatTextView = itemView.findViewById(R.id.layout_tv_release_title)


    var releaseNote: ReleaseNote? = null
        set(value) {
            field = value
            releaseTitleView.text = value?.title ?: ""

            val viewGroup = itemView as ViewGroup
            val notes = value?.notes ?: emptyList()
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

            var index = 0
            notesViews.forEach {
                val note = notes[index]
                val noteTextView: AppCompatTextView = it.findViewById(R.id.layout_tv_release_note_title)
                noteTextView.text = note

                index++
            }
        }
}
