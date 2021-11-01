package com.xeinebiu.views.changelog.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.xeinebiu.views.changelog.R
import com.xeinebiu.views.changelog.models.ReleaseNote
import java.util.LinkedList
import java.util.Queue

class ReleaseNoteRvAdapter(
    private val context: Context,
    private val releaseNotes: List<ReleaseNote>,
    @LayoutRes private val releaseTitleLayoutId: Int,
    @LayoutRes private val releaseNoteLayoutId: Int,
    @LayoutRes private val releaseDividerLayoutId: Int
) : RecyclerView.Adapter<ReleaseNoteViewHolder>() {

    private val notesRecycleViewPool: Queue<View> = LinkedList()

    private val recycleNoteView: (View) -> Unit = {
        notesRecycleViewPool.add(it)
    }

    private val requireNoteView: (ViewGroup) -> View = { parent ->
        if (notesRecycleViewPool.isEmpty()) layoutInflater.inflate(
            releaseNoteLayoutId,
            parent,
            false
        ) else notesRecycleViewPool.remove()
    }

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    override fun getItemCount(): Int = releaseNotes.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReleaseNoteViewHolder {
        // create release container
        val releaseView = layoutInflater.inflate(
            R.layout.layout_release,
            parent,
            false
        ) as ViewGroup

        // Release title
        val titleView = layoutInflater.inflate(
            releaseTitleLayoutId,
            parent,
            false
        )

        // Release divider
        val dividerView = layoutInflater.inflate(
            releaseDividerLayoutId,
            parent,
            false
        )

        releaseView.addView(titleView)
        releaseView.addView(dividerView)

        return ReleaseNoteViewHolder(
            itemView = releaseView,
            requireNoteView = requireNoteView,
            recycleNoteView = recycleNoteView
        )
    }

    override fun onBindViewHolder(
        holder: ReleaseNoteViewHolder,
        position: Int
    ) {
        val releaseNote = releaseNotes[position]

        holder.releaseNote = releaseNote
    }
}
