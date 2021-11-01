package com.xeinebiu.views.changelog.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

/**
 * Display any [View] on a [DialogFragment]
 * @author xeinebiu
 */
class ChangeLogDialogFragment : DialogFragment(), ChangeLogDialog {

    override var onClose: (() -> Unit)? = null

    override var childView: View? = null

    /**
     * Set the [DialogFragment] width to MATCH_PARENT by default
     * @author xeinebiu
     */
    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Return passed view as content for Dialog
     * @author xeinebiu
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = childView

    /**
     * Close [DialogFragment]
     */
    override fun close(): Unit = dismiss()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onClose?.invoke()
    }
}
