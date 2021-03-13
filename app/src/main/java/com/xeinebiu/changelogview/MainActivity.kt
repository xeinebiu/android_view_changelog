package com.xeinebiu.changelogview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import com.xeinebiu.views.changelog.ChangeLogManager
import java.util.concurrent.ThreadLocalRandom

class MainActivity : AppCompatActivity() {

    private var currentChangeLogManager: ChangeLogManager? = null
    private val includeFooterCheckBox by lazy { findViewById<AppCompatCheckBox>(R.id.activity_main_cb_include_footer) }

    private val changeLogs: String by lazy {
        val stringBuilder = StringBuilder()
        var feature = "#v100"
        val notes = arrayOf(
            "Feature: Recycle Views to improve memory and performance",
            "Feature: Support HTML Styling",
            "Fix: App crashes on old devices",
            "Improve: Memory Management",
            "Improve: User Experience",
            "Fix: Camera failed to open",
            "Fix: Scan of QR",
            "Improve: Image quality",
            "Improve: Barcode Scanner"
        )

        for (i in 99 downTo 1) {
            stringBuilder.appendln(feature)

            val notesCount = ThreadLocalRandom.current().nextInt(1, 10)
            for (j in 0..notesCount)
                stringBuilder.appendln(notes[ThreadLocalRandom.current().nextInt(0, notes.size)])

            feature = "#v$i"
        }

        stringBuilder.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ChangeLogManager
            .Builder(this) {
                changeLogs.byteInputStream()
            }
            .asDialog()
            .build()
            .showOnce()
    }

    fun asView(view: View): Unit =
        showAsView()

    fun asBottomSheet(view: View): Unit =
        showAsBottomSheet()

    fun asDialog(view: View): Unit =
        showAsDialog()

    fun asViewOnce(view: View): Unit =
        showAsView(true)

    fun asBottomSheetOnce(view: View): Unit =
        showAsBottomSheet(true)

    fun asDialogOnce(view: View): Unit =
        showAsDialog(true)

    private fun createBuilder(): ChangeLogManager.Builder {
        val builder = ChangeLogManager
            .Builder(this) {
                changeLogs.byteInputStream()
            }
            .withLimit(0)

        if (includeFooterCheckBox.isChecked)
            builder.withFooter(R.layout.layout_footer)

        return builder
    }

    private fun showAsView(once: Boolean = false) {
        val container: ViewGroup = findViewById(R.id.activity_main_container)
        container.removeAllViews()
        createBuilder()
            .asView(container)
            .build()
            .let {
                showReleaseNotes(it, once)
            }
    }

    private fun showAsBottomSheet(once: Boolean = false) {
        createBuilder()
            .asBottomSheet()
            .build()
            .let {
                showReleaseNotes(it, once)
            }
    }

    private fun showAsDialog(once: Boolean = false) {
        createBuilder()
            .asDialog()
            .build()
            .let {
                showReleaseNotes(it, once)
            }
    }

    private fun showReleaseNotes(changeLogManager: ChangeLogManager, once: Boolean) {
        currentChangeLogManager?.close()
        currentChangeLogManager = changeLogManager
        if (once)
            changeLogManager.showOnce { releaseShown(it) }
        else
            changeLogManager.show { releaseShown(it) }
    }

    private fun releaseShown(view: View?) {
        if (view == null) return

        view.post {
            val closeBtn: AppCompatButton? = view.findViewById(R.id.layout_footer_bt_close)
            val readMoreBtn: AppCompatButton? = view.findViewById(R.id.layout_footer_bt_read_more)

            closeBtn?.setOnClickListener { currentChangeLogManager?.close() }
            readMoreBtn?.setOnClickListener {
                Toast.makeText(
                    this,
                    getString(R.string.read_more_clicked),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
