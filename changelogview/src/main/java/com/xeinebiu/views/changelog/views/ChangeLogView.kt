package com.xeinebiu.views.changelog.views

import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.NestedScrollView
import com.xeinebiu.views.changelog.R
import com.xeinebiu.views.changelog.models.ReleaseNote
import java.io.BufferedReader
import java.io.StringReader

private fun BufferedReader.readLineTrim() = this.readLine()?.trim()

/**
 * View to display collection of [ReleaseNote]
 * @author xeinebiu
 */
class ChangeLogView(context: Context) : NestedScrollView(context) {

    /**
     * Event emitter to inform when [ReleaseNote] collection has been changed
     * @author xeinebiu
     */
    var releaseNotesChangeListener: ((View) -> Unit)? = null

    /**
     * Notes will be added to this container
     */
    private var container: LinearLayoutCompat = LinearLayoutCompat(context)

    init {
        container.layoutParams = LinearLayoutCompat.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        container.orientation = LinearLayoutCompat.VERTICAL
        addView(container)
    }


    /**
     * Show release notes
     * @param releaseNotes Release notes to display
     * @param headerLayoutId Layout Id to use as Header
     * @param releaseTitleLayoutId Layout Id to use as Title of Release
     * @param releaseNoteLayoutId Layout Id to use as Note
     * @param releaseDividerLayoutId Layout Id to use as Divider
     * @param headerText Text to set on header
     * @param footerViewLayoutId Layout Id to use as Footer (optional)
     * @param maxReleaseNotes Number of release notes to display (0 = all)
     * @author xeinebiu
     */
    fun showReleaseNotes(
        releaseNotes: String,
        @LayoutRes headerLayoutId: Int,
        @LayoutRes releaseTitleLayoutId: Int,
        @LayoutRes releaseNoteLayoutId: Int,
        @LayoutRes releaseDividerLayoutId: Int,
        headerText: String,
        @LayoutRes footerViewLayoutId: Int? = null,
        maxReleaseNotes: Int = 0
    ) {
        val result: MutableList<ReleaseNote> = ArrayList()
        BufferedReader(StringReader(releaseNotes)).use { br ->
            var line: String? = br.readLineTrim()
            while (line != null && (maxReleaseNotes <= 0 || maxReleaseNotes > result.size)) {
                if (line.length > 1 && line[0] == '#') {
                    val releaseNoteTitle = line.substring(1).trim()

                    val notes = ArrayList<String>()
                    line = br.readLineTrim()
                    while (line != null && line.isNotEmpty() && line[0] != '#') {
                        notes.add(line)
                        line = br.readLineTrim()
                    }

                    result.add(ReleaseNote(releaseNoteTitle, notes))
                } else
                    line = br.readLineTrim()
            }
        }
        showReleaseNotes(
            result,
            headerLayoutId,
            releaseTitleLayoutId,
            releaseNoteLayoutId,
            releaseDividerLayoutId,
            headerText,
            footerViewLayoutId
        )
    }

    /**
     * Show release notes
     * @param releaseNotes Release notes to display
     * @param headerLayoutId Layout Id to use as Header
     * @param releaseTitleLayoutId Layout Id to use as Title of Release
     * @param releaseNoteLayoutId Layout Id to use as Note
     * @param releaseDividerLayoutId Layout Id to use as Divider
     * @param headerText Text to set on header
     * @param footerViewLayoutId Layout Id to use as Footer (optional)
     * @author xeinebiu
     */
    private fun showReleaseNotes(
        releaseNotes: List<ReleaseNote>,
        @LayoutRes headerLayoutId: Int,
        @LayoutRes releaseTitleLayoutId: Int,
        @LayoutRes releaseNoteLayoutId: Int,
        @LayoutRes releaseDividerLayoutId: Int,
        headerText: String,
        @LayoutRes footerViewLayoutId: Int? = null
    ) {
        post {
            // clean old views
            clearReleaseNotes()

            // add header
            val header = inflate(headerLayoutId)
            val headerTitleView: AppCompatTextView = header.findViewById(R.id.layout_tv_title)
            headerTitleView.text = formatText(headerText)
            container.addView(header)

            releaseNotes.forEach { rn ->
                // create release container
                val releaseView = inflate(R.layout.layout_release) as ViewGroup

                // add release title
                val titleView = inflate(releaseTitleLayoutId)
                val titleTextView: AppCompatTextView = titleView.findViewById(R.id.layout_tv_release_title)
                titleTextView.text = formatText(rn.title)
                releaseView.addView(titleView)

                // add all notes
                rn.notes.forEach { n ->
                    val noteView = inflate(releaseNoteLayoutId)
                    val noteTextView: AppCompatTextView = noteView.findViewById(R.id.layout_tv__release_note_title)
                    noteTextView.text = formatText(n)
                    releaseView.addView(noteView)
                }

                // add divider
                val dividerView = inflate(releaseDividerLayoutId)
                releaseView.addView(dividerView)

                container.addView(releaseView)
            }

            // add footer if present
            if (footerViewLayoutId != null && footerViewLayoutId != 0) {
                val footerView = inflate(footerViewLayoutId)
                container.addView(footerView)
            }

            container.post {
                releaseNotesChangeListener?.invoke(this)
            }
        }
    }

    /**
     * Remove all [ReleaseNote] from the [container]
     * @author xeinebiu
     */
    private fun clearReleaseNotes(): Unit =
        container.removeAllViews()

    /**
     * Format given text from HTML
     * @param text Text to format
     * @author xeinebiu
     */
    private fun formatText(text: String): Spanned =
        HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

    /**
     * Inflate layout
     * @param layoutRes Resource Id for the Layout
     * @author xeinebiu
     */
    private fun inflate(@LayoutRes layoutRes: Int): View =
        LayoutInflater.from(context).inflate(layoutRes, container, false)
}
