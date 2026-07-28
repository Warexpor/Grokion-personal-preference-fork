package io.github.stardomains3.oxproxion

import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object GrokInputDialog {

    fun show(
        fragment: Fragment,
        title: String,
        hint: String,
        initialText: String,
        confirmText: String,
        onConfirm: (String) -> Unit
    ) {
        val context = fragment.requireContext()
        val dialog = MaterialAlertDialogBuilder(
            context,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
        ).create()

        val sheet = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_input, null)
        sheet.findViewById<TextView>(R.id.inputTitle).text = title
        val inputLayout = sheet.findViewById<TextInputLayout>(R.id.inputLayout)
        val inputField = sheet.findViewById<TextInputEditText>(R.id.inputField)
        inputLayout.hint = hint
        inputField.setText(initialText)
        inputField.setSelection(initialText.length)

        val confirmButton = sheet.findViewById<MaterialButton>(R.id.inputConfirm)
        confirmButton.text = confirmText
        confirmButton.setTextColor(ContextCompat.getColor(context, R.color.xai_ink))

        sheet.findViewById<MaterialButton>(R.id.inputCancel).setOnClickListener {
            dialog.dismiss()
        }
        confirmButton.setOnClickListener {
            dialog.dismiss()
            onConfirm(inputField.text?.toString().orEmpty())
        }

        dialog.setView(sheet)
        dialog.window?.setDimAmount(0.72f)
        dialog.show()
        inputField.requestFocus()
    }
}
