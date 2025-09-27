package com.example.brainnode.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.example.brainnode.R

class CustomSubjectDialog(
    private val context: Context,
    private val onContinue: (String) -> Unit,
    private val onCancel: () -> Unit = {}
) {
    
    private var dialog: AlertDialog? = null
    
    fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_custom_subject_input, null)
        
        val etSubjectName = view.findViewById<EditText>(R.id.etSubjectName)
        val btnContinue = view.findViewById<Button>(R.id.btnContinue)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        
        dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(true)
            .create()
        
        // Set click listeners
        btnContinue.setOnClickListener {
            val subjectName = etSubjectName.text.toString().trim()
            if (subjectName.isNotEmpty()) {
                onContinue(subjectName)
                dialog?.dismiss()
            } else {
                etSubjectName.error = "Please enter a subject name"
            }
        }
        
        btnCancel.setOnClickListener {
            onCancel()
            dialog?.dismiss()
        }
        
        dialog?.show()
    }
    
    fun dismiss() {
        dialog?.dismiss()
    }
}
