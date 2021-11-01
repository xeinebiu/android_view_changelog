package com.xeinebiu.views.changelog.dialogs

import android.view.View

/**
 * Base Logic to show any view on Dialog's
 * @author xeinebiu
 */
interface ChangeLogDialog {

    /**
     * Called when the dialog is closed
     */
    var onClose: (() -> Unit)?

    /**
     * [View] to display as Dialog Content
     */
    var childView: View?

    /**
     * Close Dialog
     * @author xeinebiu
     */
    fun close()

    /**
     * Initialize the [ChangeLogDialog] with a [View] to set as content
     * @author xeinebiu
     */
    fun init(view: View) {
        if (childView != null) throw Exception(
            "ChangeLogDialog is already initialized once, consider creating another dialog for a different view!"
        )

        childView = view
    }
}
