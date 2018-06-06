package com.corphish.nightlight.design.alert

import android.content.Context
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.appcompat.widget.AppCompatButton
import android.view.View
import android.widget.TextView

import com.corphish.nightlight.R

/**
 * Created by avinabadalal on 06/03/18.
 * Bottom sheet alert dialog
 */

class BottomSheetAlertDialog(val context: Context) {
    private var contentView = View.inflate(context, R.layout.bottom_sheet_msg, null)
    private var title: TextView
    private var content: TextView
    private var negativeButton: TextView
    private var positiveButton: AppCompatButton
    private var neutralButton: AppCompatButton

    private var bottomSheetDialog: BottomSheetDialog

    init {
        title = contentView.findViewById(R.id.title)
        content = contentView.findViewById(R.id.content)
        negativeButton = contentView.findViewById(R.id.negativeButton)
        positiveButton = contentView.findViewById(R.id.positiveButton)
        neutralButton = contentView.findViewById(R.id.neutralButton)

        bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogDark)
    }

    fun setTitle(@StringRes iTitle: Int): BottomSheetAlertDialog {
        title.setText(iTitle)

        return this
    }

    fun setTitle(sTitle: String): BottomSheetAlertDialog {
        title.text = sTitle

        return this
    }

    fun setMessage(@StringRes iTitle: Int): BottomSheetAlertDialog {
        content.setText(iTitle)

        return this
    }

    fun setMessage(sTitle: String): BottomSheetAlertDialog {
        content.text = sTitle

        return this
    }

    fun setPositiveButton(@StringRes iTitle: Int, onClickListener: View.OnClickListener): BottomSheetAlertDialog {
        positiveButton.visibility = View.VISIBLE
        positiveButton.setText(iTitle)
        positiveButton.setOnClickListener { view ->
            onClickListener.onClick(view)
            bottomSheetDialog.dismiss()
        }

        return this
    }

    fun setNegativeButton(@StringRes iTitle: Int, onClickListener: View.OnClickListener): BottomSheetAlertDialog {
        negativeButton.visibility = View.VISIBLE
        negativeButton.setText(iTitle)
        negativeButton.setOnClickListener { view ->
            onClickListener.onClick(view)
            bottomSheetDialog.dismiss()
        }

        return this
    }

    fun setNeutralButton(@StringRes iTitle: Int, onClickListener: View.OnClickListener): BottomSheetAlertDialog {
        neutralButton.visibility = View.VISIBLE
        neutralButton.setText(iTitle)
        neutralButton.setOnClickListener { view ->
            onClickListener.onClick(view)
            bottomSheetDialog.dismiss()
        }

        return this
    }

    fun setCancelable(b: Boolean): BottomSheetAlertDialog {
        bottomSheetDialog.setCancelable(b)

        return this
    }

    fun show(): Boolean {
        bottomSheetDialog.setContentView(contentView)
        bottomSheetDialog.show()

        return true
    }
}