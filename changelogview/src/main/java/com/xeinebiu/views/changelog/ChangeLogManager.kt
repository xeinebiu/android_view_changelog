package com.xeinebiu.views.changelog

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.xeinebiu.views.changelog.dialogs.ChangeLogBottomSheetDialogFragment
import com.xeinebiu.views.changelog.dialogs.ChangeLogDialog
import com.xeinebiu.views.changelog.dialogs.ChangeLogDialogFragment
import com.xeinebiu.views.changelog.views.ChangeLogView
import java.io.InputStream


/**
 * <p>Manager for the Change Logs</p>
 * <p>Manage how the Change Logs are shown</p>
 * @author xeinebiu
 */
class ChangeLogManager constructor(
    private val activity: AppCompatActivity,
    private val changeLogView: ChangeLogView,
    private val releaseNotes: () -> InputStream,
    private val type: Type,
    private val parentContainer: ViewGroup?
) {
    private var dialog: ChangeLogDialog? = null

    /**
     * Close Release Notes
     * @author xeinebiu
     */
    fun close() {
        if (type == Type.View)
            (changeLogView.parent as ViewGroup?)?.removeView(changeLogView)
        else
            dialog?.close()
    }

    /**
     * Show Release Notes unconditionally
     * @author xeinebiu
     */
    fun show(callback: ((View) -> Unit)? = null) {
        changeLogView.releaseNotesChangeListener = callback
        changeLogView.showReleaseNotes(releaseNotes)
        when (type) {
            Type.View -> showOnContainer(changeLogView)
            Type.BottomSheet -> dialog = showBottomsheetDialog(changeLogView)
            Type.Dialog -> dialog = showDialog(changeLogView)
        }
    }

    /**
     * Show Release Notes only if not shown yet for current application version
     * @author xeinebiu
     */
    fun showOnce(callback: ((View) -> Unit)? = null) {
        val appVersionCode = getAppVersionCode()
        val lastAppVersionCode = getLastAppVersionCode()
        if (appVersionCode > lastAppVersionCode) {
            setLastAppVersionCode(appVersionCode)
            show(callback)
        }
    }

    private fun showOnContainer(view: ChangeLogView): View {
        parentContainer?.addView(view)
        return view
    }

    private fun showDialog(view: ChangeLogView): ChangeLogDialog {
        val dialog = ChangeLogDialogFragment()
        return showDialog(dialog, dialog, view)
    }

    private fun showBottomsheetDialog(view: ChangeLogView): ChangeLogDialog {
        val dialog = ChangeLogBottomSheetDialogFragment()
        return showDialog(dialog, dialog, view)
    }

    private fun showDialog(
        changeLogDialog: ChangeLogDialog,
        dialog: DialogFragment,
        view: View
    ): ChangeLogDialog {
        changeLogDialog.init(view)
        dialog.show(activity.supportFragmentManager, null)
        return changeLogDialog
    }

    private fun getAppVersionCode(): Long {
        val pInfo: PackageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            pInfo.longVersionCode
        else
            pInfo.versionCode.toLong()
    }

    private fun getLastAppVersionCode(): Long {
        val preferences = getSharedPreferences()
        return preferences.getLong(PREFERENCES_KEY_APP_VERSION, 0)
    }

    private fun setLastAppVersionCode(versionCode: Long) {
        val preferences = getSharedPreferences()
        with(preferences.edit()) {
            putLong(PREFERENCES_KEY_APP_VERSION, versionCode)
            apply()
        }
    }

    private fun getSharedPreferences(): SharedPreferences =
        activity.applicationContext.getSharedPreferences(PREFERENCES_GROUP, Context.MODE_PRIVATE)

    companion object {
        private const val PREFERENCES_GROUP = "com.xeinebiu.views.changelog"
        private const val PREFERENCES_KEY_APP_VERSION = "app.version"
    }

    /**
     * Display type of [com.xeinebiu.views.changelog.models.ReleaseNote]
     */
    enum class Type {
        View,
        Dialog,
        BottomSheet
    }

    class Builder constructor(
        private val activity: AppCompatActivity,
        private val releaseNotes: () -> InputStream
    ) {
        private val changeLogView = ChangeLogView(activity).apply {
            headerLayoutId = R.layout.layout_title
            releaseTitleLayoutId = R.layout.layout_release_title
            releaseNoteLayoutId = R.layout.layout_release_note
            releaseDividerLayoutId = R.layout.layout_release_divider
            headerText = "Change Logs"
        }.also {
            it.layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
        }

        private var type = Type.Dialog
        private var parentContainer: ViewGroup? = null

        /**
         * Display Release Notes on a [com.google.android.material.bottomsheet.BottomSheetDialogFragment]
         * @author xeinebiu
         */
        fun asBottomSheet() = apply {
            type = Type.BottomSheet
        }

        /**
         * Display Release Notes on a [DialogFragment]
         * @author xeinebiu
         */
        fun asDialog() = apply {
            type = Type.Dialog
        }

        /**
         * Display Release Notes on a [ViewGroup]
         * @author xeinebiu
         */
        fun asView(container: ViewGroup) = apply {
            parentContainer = container
            type = Type.View
            return this
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
        fun build(): ChangeLogManager =
            ChangeLogManager(
                activity,
                changeLogView,
                releaseNotes,
                type,
                parentContainer
            )
    }
}
