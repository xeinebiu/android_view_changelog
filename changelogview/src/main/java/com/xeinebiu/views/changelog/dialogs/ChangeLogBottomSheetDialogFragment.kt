package com.xeinebiu.views.changelog.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Display any [View] on a [BottomSheetDialogFragment]
 * @author xeinebiu
 */
class ChangeLogBottomSheetDialogFragment : BottomSheetDialogFragment(), ChangeLogDialog {
    override var childView: View? = null

    /**
     * Return passed view as content for Dialog
     * @author xeinebiu
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        childView

    /**
     * Make [Dialog] HALF EXPANDED by default
     * @author xeinebiu
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dia: DialogInterface ->
            val dialog = dia as BottomSheetDialog
            val bottomSheet: ViewGroup? = dialog.findViewById(R.id.design_bottom_sheet)
            if (bottomSheet != null)
                BottomSheetBehavior.from<ViewGroup>(bottomSheet).state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        return bottomSheetDialog
    }

    /**
     * Close [BottomSheetDialogFragment]
     */
    override fun close(): Unit =
        dismiss()
}
