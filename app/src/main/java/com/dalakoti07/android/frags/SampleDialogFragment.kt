package com.dalakoti07.android.frags

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.dalakoti07.android.R

class SampleDialogFragment : DialogFragment(R.layout.fragment_dialog){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btn_close_dialog).setOnClickListener {
            dismiss()
        }
    }

    override fun dismiss() {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "ORDER_ITEMS_ARGUMENT_KEY",
            "hello"
        )
        super.dismiss()
    }

}