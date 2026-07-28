package io.github.stardomains3.oxproxion

import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object GrokConfirmDialog {

    fun show(
        fragment: Fragment,
        title: String,
        message: String,
        confirmText: String,
        onConfirm: () -> Unit,
        destructive: Boolean = true
    ) {
        val context = fragment.requireContext()
        val dialog = MaterialAlertDialogBuilder(
            context,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        ).create()

        val sheet = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_action, null)
        sheet.findViewById<TextView>(R.id.confirmTitle).text = title
        sheet.findViewById<TextView>(R.id.confirmMessage).text = message

        val actionButton = sheet.findViewById<MaterialButton>(R.id.confirmAction)
        actionButton.text = confirmText
        if (destructive) {
            actionButton.setTextColor(ContextCompat.getColor(context, R.color.xai_error))
        } else {
            actionButton.setTextColor(ContextCompat.getColor(context, R.color.xai_ink))
        }

        sheet.findViewById<MaterialButton>(R.id.confirmCancel).setOnClickListener {
            dialog.dismiss()
        }
        actionButton.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }

        dialog.setView(sheet)
        dialog.window?.setDimAmount(0.72f)
        dialog.show()
    }
}
