package io.github.stardomains3.oxproxion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.Fragment

class EditMessageFragment : Fragment() {

    private lateinit var contentEditText: TextInputEditText

    companion object {
        private const val ARG_POSITION = "position"
        private const val ARG_CONTENT = "content"

        fun newInstance(position: Int, content: String): EditMessageFragment {
            val args = Bundle().apply {
                putInt(ARG_POSITION, position)
                putString(ARG_CONTENT, content)
            }
            return EditMessageFragment().apply { arguments = args }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        contentEditText = view.findViewById(R.id.edit_text_message_content)
        val saveButton = view.findViewById<MaterialButton>(R.id.btnSaveEdit)
        val cancelButton = view.findViewById<MaterialButton>(R.id.btnCancelEdit)

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
        cancelButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val content = arguments?.getString(ARG_CONTENT) ?: ""
        contentEditText.setText(content)
        contentEditText.setSelection(content.length)

        saveButton.setOnClickListener {
            val newContent = contentEditText.text?.toString()?.trim().orEmpty()
            val position = arguments?.getInt(ARG_POSITION) ?: -1
            if (newContent.isNotBlank() && position != -1) {
                parentFragmentManager.setFragmentResult(
                    "edit_request_key",
                    Bundle().apply {
                        putInt("position", position)
                        putString("content", newContent)
                    }
                )
                parentFragmentManager.popBackStack()
            }
        }
    }
}
