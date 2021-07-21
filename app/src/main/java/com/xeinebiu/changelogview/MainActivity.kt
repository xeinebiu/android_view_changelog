package com.xeinebiu.changelogview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.lifecycle.lifecycleScope
import com.xeinebiu.views.changelog.ChangeLogManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ThreadLocalRandom

class MainActivity : AppCompatActivity() {

    private var currentChangeLogManager: ChangeLogManager? = null

    private val includeFooterCheckBox by lazy {
        findViewById<AppCompatCheckBox>(R.id.activity_main_cb_include_footer)
    }

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
                stringBuilder.appendLine(notes[ThreadLocalRandom.current().nextInt(0, notes.size)])

            feature = "#v$i"
        }

        stringBuilder.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        lifecycleScope.launch(Dispatchers.Default) {
            ChangeLogManager
                .Builder(
                    context = this@MainActivity,
                    type = ChangeLogManager.Type.Dialog(supportFragmentManager)
                ) {
                    changeLogs.byteInputStream()
                }
                .build()
                .showOnce()
        }
    }

    fun asView(view: View) {
        lifecycleScope.launch { showAsView() }
    }

    fun asBottomSheet(view: View) {
        lifecycleScope.launch { showAsBottomSheet() }
    }

    fun asDialog(view: View) {
        lifecycleScope.launch { showAsDialog() }
    }

    fun asViewOnce(view: View) {
        lifecycleScope.launch { showAsView(true) }
    }

    fun asBottomSheetOnce(view: View) {
        lifecycleScope.launch { showAsBottomSheet(true) }
    }

    fun asDialogOnce(view: View) {
        lifecycleScope.launch { showAsDialog(true) }
    }

    private fun createBuilder(type: ChangeLogManager.Type): ChangeLogManager.Builder {
        val builder = ChangeLogManager
            .Builder(this, type) {
                changeLogs.byteInputStream()
            }
            .withLimit(0)

        if (includeFooterCheckBox.isChecked) builder.withFooter(R.layout.layout_footer)

        return builder
    }

    private suspend fun showAsView(once: Boolean = false) {
        val container: ViewGroup = findViewById(R.id.activity_main_container)
        container.removeAllViews()

        createBuilder(ChangeLogManager.Type.View(container))
            .build()
            .also {
                showReleaseNotes(it, once)
            }
    }

    private suspend fun showAsBottomSheet(once: Boolean = false) {
        createBuilder(ChangeLogManager.Type.BottomSheet(supportFragmentManager))
            .build()
            .also {
                showReleaseNotes(it, once)
            }
    }

    private suspend fun showAsDialog(once: Boolean = false) {
        createBuilder(ChangeLogManager.Type.Dialog(supportFragmentManager))
            .build()
            .also {
                showReleaseNotes(it, once)
            }
    }

    private suspend fun showReleaseNotes(
        changeLogManager: ChangeLogManager,
        once: Boolean
    ) {
        currentChangeLogManager?.close()

        currentChangeLogManager = changeLogManager

        if (once) changeLogManager.showOnce()
        else changeLogManager.show()
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
