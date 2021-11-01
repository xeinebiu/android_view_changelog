package com.xeinebiu.views.changelog.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Display any [View] on a [BottomSheetDialogFragment]
 * @author xeinebiu
 */
class ChangeLogBottomSheetDialogFragment : BottomSheetDialogFragment(), ChangeLogDialog {

    override var onClose: (() -> Unit)? = null

    override var childView: View? = null

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
     * Close [BottomSheetDialogFragment]
     */
    override fun close(): Unit = dismiss()

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        onClose?.invoke()
    }
}
