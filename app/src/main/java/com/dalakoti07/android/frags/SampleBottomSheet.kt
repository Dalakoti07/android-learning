package com.dalakoti07.android.frags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.dalakoti07.android.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SampleBottomSheet: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btn_open_dialog).setOnClickListener {
            dismiss()
            findNavController().navigate(
                R.id.action_sampleBottomSheet_to_sampleDialogFragment
            )
        }
    }

}