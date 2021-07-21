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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xeinebiu.views.changelog.R
import com.xeinebiu.views.changelog.adapters.ReleaseNoteRvAdapter
import com.xeinebiu.views.changelog.models.ReleaseNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.BufferedReader
import java.io.InputStream

private fun BufferedReader.readLineTrim() = this.readLine()?.trim()

/**
 * View to display collection of [ReleaseNote]
 * @author xeinebiu
 */
class ChangeLogView(context: Context) : LinearLayoutCompat(context) {

    @LayoutRes
    var headerLayoutId: Int = 0

    @LayoutRes
    var releaseTitleLayoutId: Int = 0

    @LayoutRes
    var releaseNoteLayoutId: Int = 0

    @LayoutRes
    var releaseDividerLayoutId: Int = 0

    var headerText: String = ""

    @LayoutRes
    var footerViewLayoutId: Int = 0

    var maxReleaseNotes: Int = -1

    init {
        orientation = VERTICAL
    }

    /**
     * Show release notes
     * @param releaseNotes Release notes to display
     * @author xeinebiu
     */
    suspend fun showReleaseNotes(releaseNotes: suspend () -> InputStream) {
        val result = withContext(Dispatchers.IO) {
            val result = mutableListOf<ReleaseNote>()

            releaseNotes().bufferedReader().use { br ->
                var line: String? = br.readLineTrim()

                while (line != null) {
                    yield()

                    if (result.size >= maxReleaseNotes && maxReleaseNotes > 0) return@use

                    if (line.length > 1 && line[0] == '#') {
                        val releaseNoteTitle = line.substring(1).trim()

                        val notes = ArrayList<String>()
                        line = br.readLineTrim()

                        while (line != null && line.isNotEmpty() && line[0] != '#') {
                            notes.add(line)
                            line = br.readLineTrim()
                        }

                        result.add(ReleaseNote(releaseNoteTitle, notes))
                    } else {
                        line = br.readLineTrim()
                    }
                }
            }

            result
        }

        showReleaseNotes(result)
    }

    /**
     * Show release notes
     * @param releaseNotes Release notes to display
     * @author xeinebiu
     */
    private suspend fun showReleaseNotes(releaseNotes: List<ReleaseNote>) {
        withContext(Dispatchers.Main) {
            // clean old views
            clearReleaseNotes()

            // add header
            val header = inflate(headerLayoutId)
            val headerTitleView: AppCompatTextView = header.findViewById(R.id.layout_tv_title)
            headerTitleView.text = formatText(headerText)
            addView(header)

            // setup recycler view
            val recyclerView = createRecyclerView()
            addView(recyclerView)
            showReleaseNotes(recyclerView, releaseNotes)

            // add footer if present
            if (footerViewLayoutId != 0) {
                val footerView = inflate(footerViewLayoutId)
                addView(footerView)
            }
        }
    }

    /**
     * Show given [ReleaseNote] collection on a [RecyclerView]
     * @author xeinebiu
     */
    private fun showReleaseNotes(
        recyclerView: RecyclerView,
        releaseNotes: List<ReleaseNote>
    ) {
        val adapter = ReleaseNoteRvAdapter(
            context = context,
            releaseNotes = releaseNotes,
            releaseTitleLayoutId = releaseTitleLayoutId,
            releaseNoteLayoutId = releaseNoteLayoutId,
            releaseDividerLayoutId = releaseDividerLayoutId
        )

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        recyclerView.adapter = adapter
    }

    /**
     * Create [RecyclerView] instance
     * @author xeinebiu
     */
    private fun createRecyclerView(): RecyclerView =
        RecyclerView(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1.0f
            )
        }

    /**
     * Remove all [ReleaseNote]
     * @author xeinebiu
     */
    private fun clearReleaseNotes(): Unit = removeAllViews()

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
        LayoutInflater.from(context).inflate(layoutRes, this, false)
}
