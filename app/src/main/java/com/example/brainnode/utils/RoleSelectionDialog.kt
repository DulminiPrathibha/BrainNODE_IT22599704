package com.example.brainnode.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.example.brainnode.R
import com.example.brainnode.data.models.UserType

class RoleSelectionDialog(
    private val context: Context,
    private val onRoleSelected: (UserType) -> Unit
) {
    
    private var dialog: Dialog? = null
    
    fun show() {
        dialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_role_selection, null)
        
        dialog?.apply {
            setContentView(view)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        
        setupClickListeners(view)
        dialog?.show()
    }
    
    private fun setupClickListeners(view: View) {
        val btnStudent = view.findViewById<Button>(R.id.btnStudent)
        val btnTeacher = view.findViewById<Button>(R.id.btnTeacher)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        
        btnStudent.setOnClickListener {
            onRoleSelected(UserType.STUDENT)
            dismiss()
        }
        
        btnTeacher.setOnClickListener {
            onRoleSelected(UserType.TEACHER)
            dismiss()
        }
        
        btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }
}
