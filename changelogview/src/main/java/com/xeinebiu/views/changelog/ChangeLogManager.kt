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


/**
 * <p>Manager for the Change Logs</p>
 * <p>Manage how the Change Logs are shown</p>
 * @author xeinebiu
 */
class ChangeLogManager constructor(
    private val activity: AppCompatActivity,
    private val releaseNotes: String,
    @LayoutRes private val headerLayoutId: Int,
    @LayoutRes private val releaseTitleLayoutId: Int,
    @LayoutRes private val releaseNoteLayoutId: Int,
    @LayoutRes private val releaseDividerLayoutId: Int,
    private val headerText: String,
    private val type: Type,
    private val parentContainer: ViewGroup?,
    @LayoutRes private val footerLayoutId: Int?,
    private val maxReleaseNotes: Int
) {
    private var changeLogView: View? = null
    private var dialog: ChangeLogDialog? = null

    /**
     * Close Release Notes
     * @author xeinebiu
     */
    fun close() {
        val view = changeLogView
        if (type == Type.View && view != null)
            (view.parent as ViewGroup?)?.removeView(view)
        else
            dialog?.close()
    }

    /**
     * Show Release Notes unconditionally
     * @author xeinebiu
     */
    fun show(callback: ((View) -> Unit)? = null) {
        val view = ChangeLogView(activity)
        view.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.releaseNotesChangeListener = callback
        view.showReleaseNotes(
            releaseNotes,
            headerLayoutId,
            releaseTitleLayoutId,
            releaseNoteLayoutId,
            releaseDividerLayoutId,
            headerText,
            footerLayoutId,
            maxReleaseNotes
        )
        changeLogView = view
        when (type) {
            Type.View -> showOnContainer(view)
            Type.BottomSheet -> dialog = showBottomsheetDialog(view)
            Type.Dialog -> dialog = showDialog(view)
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

    private fun showDialog(changeLogDialog: ChangeLogDialog, dialog: DialogFragment, view: View): ChangeLogDialog {
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
        private val releaseNotes: String
    ) {
        @LayoutRes
        private var headerLayoutId: Int = R.layout.layout_title

        @LayoutRes
        private var releaseTitleLayoutId = R.layout.layout_release_title

        @LayoutRes
        private var releaseNoteLayoutId = R.layout.layout_release_note

        @LayoutRes
        private var releaseDividerLayoutId = R.layout.layout_release_divider

        @LayoutRes
        private var footerLayoutId: Int? = null
        private var type = Type.Dialog
        private var parentContainer: ViewGroup? = null
        private var headerText = "Change Logs"
        private var maxReleaseNotes = 0

        /**
         * Display Release Notes on a [com.google.android.material.bottomsheet.BottomSheetDialogFragment]
         * @author xeinebiu
         */
        fun asBottomSheet(): Builder {
            type = Type.BottomSheet
            return this
        }

        /**
         * Display Release Notes on a [DialogFragment]
         * @author xeinebiu
         */
        fun asDialog(): Builder {
            type = Type.Dialog
            return this
        }

        /**
         * Display Release Notes on a [ViewGroup]
         * @author xeinebiu
         */
        fun asView(container: ViewGroup): Builder {
            parentContainer = container
            type = Type.View
            return this
        }

        /**
         * Limit amount of Release notes to show
         * @author xeinebiu
         */
        fun withLimit(limit: Int): Builder {
            maxReleaseNotes = limit
            return this
        }

        /**
         * Display [View] as Footer
         * @author xeinebiu
         */
        fun withFooter(@LayoutRes layoutId: Int): Builder {
            footerLayoutId = layoutId
            return this
        }

        /**
         * Set the [layoutId] to use as divider between Release's
         * @author xeinebiu
         */
        fun withReleaseDivider(@LayoutRes layoutId: Int): Builder {
            releaseDividerLayoutId = layoutId
            return this
        }

        /**
         * Set the [layoutId] to use for Release Note
         * @author xeinebiu
         */
        fun withReleaseNote(@LayoutRes layoutId: Int): Builder {
            releaseNoteLayoutId = layoutId
            return this
        }

        /**
         * Set the [layoutId] to use for Release Title
         * @author xeinebiu
         */
        fun withReleaseTitle(@LayoutRes layoutId: Int): Builder {
            releaseTitleLayoutId = layoutId
            return this
        }

        /**
         * Set text to display on the header
         * @author xeinebiu
         */
        fun withHeaderText(text: String): Builder {
            headerText = text
            return this
        }

        /**
         * Set the [layoutId] for Header
         * @author xeinebiu
         */
        fun withHeader(@LayoutRes layoutId: Int): Builder {
            headerLayoutId = layoutId
            return this
        }

        /**
         * Build the [ChangeLogManager]
         * @author xeinebiu
         */
        fun build(): ChangeLogManager =
            ChangeLogManager(
                activity,
                releaseNotes,
                headerLayoutId,
                releaseTitleLayoutId,
                releaseNoteLayoutId,
                releaseDividerLayoutId,
                headerText,
                type,
                parentContainer,
                footerLayoutId,
                maxReleaseNotes
            )

        companion object {
            /**
             * Start a builder using an [AppCompatActivity] and [releaseNotes]
             * @author xeinebiu
             */
            fun with(activity: AppCompatActivity, releaseNotes: String): Builder =
                Builder(activity, releaseNotes)
        }
    }
}
