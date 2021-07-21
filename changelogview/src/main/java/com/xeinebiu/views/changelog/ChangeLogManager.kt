package com.xeinebiu.views.changelog

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.content.edit
import androidx.fragment.app.FragmentManager
import com.xeinebiu.views.changelog.dialogs.ChangeLogBottomSheetDialogFragment
import com.xeinebiu.views.changelog.dialogs.ChangeLogDialog
import com.xeinebiu.views.changelog.dialogs.ChangeLogDialogFragment
import com.xeinebiu.views.changelog.views.ChangeLogView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

/**
 * <p>Manager for the Change Logs</p>
 * <p>Manage how the Change Logs are shown</p>
 * @author xeinebiu
 */
class ChangeLogManager(
    private val changeLogView: ChangeLogView,
    private val context: Context,
    private val releaseNotes: suspend () -> InputStream,
    private val type: Type,
) {

    private var dialog: ChangeLogDialog? = null

    /**
     * Close Release Notes
     * @author xeinebiu
     */
    fun close() {
        if (type is Type.View) (changeLogView.parent as ViewGroup?)?.removeView(changeLogView)
        else dialog?.close()
    }

    /**
     * Show Release Notes unconditionally
     * @author xeinebiu
     */
    suspend fun show() {
        changeLogView.showReleaseNotes(releaseNotes)

        withContext(Dispatchers.Main) {
            when (type) {
                is Type.View -> showOnContainer(
                    parent = type.parent,
                    view = changeLogView
                )

                is Type.BottomSheet -> showBottomSheetDialog(
                    fragmentManager = type.fragmentManager,
                    view = changeLogView
                ).also {
                    dialog = it
                }

                is Type.Dialog -> showDialog(
                    fragmentManager = type.fragmentManager,
                    view = changeLogView
                ).also {
                    dialog = it
                }
            }
        }
    }

    /**
     * Show Release Notes only if not shown yet for current application version
     * @author xeinebiu
     */
    suspend fun showOnce() {
        val appVersionCode = getAppVersionCode()
        val lastAppVersionCode = getLastAppVersionCode()

        if (appVersionCode > lastAppVersionCode) {
            setLastAppVersionCode(appVersionCode)
            show()
        }
    }

    private fun getAppVersionCode(): Long {
        val pInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pInfo.longVersionCode
        else pInfo.versionCode.toLong()
    }

    private fun getLastAppVersionCode(): Long {
        val preferences = getSharedPreferences()
        return preferences.getLong(PREFERENCES_KEY_APP_VERSION, 0)
    }

    private fun getSharedPreferences(): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFERENCES_GROUP, Context.MODE_PRIVATE)

    private fun setLastAppVersionCode(versionCode: Long) {
        getSharedPreferences().edit {
            putLong(PREFERENCES_KEY_APP_VERSION, versionCode)
        }
    }

    private fun showBottomSheetDialog(
        fragmentManager: FragmentManager,
        view: ChangeLogView
    ): ChangeLogDialog {
        val dialog = ChangeLogBottomSheetDialogFragment().also {
            it.init(view)
        }

        dialog.show(fragmentManager, null)

        return dialog
    }

    private fun showDialog(
        fragmentManager: FragmentManager,
        view: ChangeLogView
    ): ChangeLogDialog {
        val dialog = ChangeLogDialogFragment().also {
            it.init(view)
        }

        dialog.show(fragmentManager, null)

        return dialog
    }

    private fun showOnContainer(
        parent: ViewGroup,
        view: ChangeLogView
    ): View {
        parent.addView(view)
        return view
    }

    class Builder constructor(
        private val context: Context,
        private val type: Type,
        private val releaseNotes: suspend () -> InputStream
    ) {
        private val changeLogView = ChangeLogView(context).apply {
            headerLayoutId = R.layout.layout_title

            releaseTitleLayoutId = R.layout.layout_release_title

            releaseNoteLayoutId = R.layout.layout_release_note

            releaseDividerLayoutId = R.layout.layout_release_divider

            headerText = "Change Logs"

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        /**
         * Limit amount of Release notes to show
         * @author xeinebiu
         */
        fun withLimit(limit: Int) = apply {
            changeLogView.maxReleaseNotes = limit
        }

        /**
         * Display [View] as Footer
         * @author xeinebiu
         */
        fun withFooter(@LayoutRes layoutId: Int) = apply {
            changeLogView.footerViewLayoutId = layoutId
        }

        /**
         * Set the [layoutId] to use as divider between Release's
         * @author xeinebiu
         */
        fun withReleaseDivider(@LayoutRes layoutId: Int) = apply {
            changeLogView.releaseDividerLayoutId = layoutId
        }

        /**
         * Set the [layoutId] to use for Release Note
         * @author xeinebiu
         */
        fun withReleaseNote(@LayoutRes layoutId: Int) = apply {
            changeLogView.releaseNoteLayoutId = layoutId
        }

        /**
         * Set the [layoutId] to use for Release Title
         * @author xeinebiu
         */
        fun withReleaseTitle(@LayoutRes layoutId: Int) = apply {
            changeLogView.releaseTitleLayoutId = layoutId
        }

        /**
         * Set text to display on the header
         * @author xeinebiu
         */
        fun withHeaderText(text: String) = apply {
            changeLogView.headerText = text
        }

        /**
         * Set the [layoutId] for Header
         * @author xeinebiu
         */
        fun withHeader(@LayoutRes layoutId: Int) = apply {
            changeLogView.headerLayoutId = layoutId
        }

        /**
         * Build the [ChangeLogManager]
         * @author xeinebiu
         */
        fun build() = ChangeLogManager(
            context = context,
            changeLogView = changeLogView,
            releaseNotes = releaseNotes,
            type = type
        )
    }

    /**
     * Display type of [com.xeinebiu.views.changelog.models.ReleaseNote]
     */
    sealed class Type {

        /**
         * Display [ChangeLogView] inside [parent]
         */
        data class View(val parent: ViewGroup) : Type()

        /**
         * Display [ChangeLogView] as Dialog using [fragmentManager]
         */
        data class Dialog(val fragmentManager: FragmentManager) : Type()

        /**
         * Display [ChangeLogView] as Bottom Sheet using [fragmentManager]
         */
        data class BottomSheet(val fragmentManager: FragmentManager) : Type()
    }

    companion object {
        private const val PREFERENCES_GROUP = "com.xeinebiu.views.changelog"
        private const val PREFERENCES_KEY_APP_VERSION = "app.version"
    }
}
