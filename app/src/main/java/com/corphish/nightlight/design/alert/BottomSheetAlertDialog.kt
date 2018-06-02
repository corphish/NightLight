package com.corphish.nightlight.design.alert

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.AppCompatButton
import android.view.View
import android.widget.TextView

import com.corphish.nightlight.R

/**
 * Created by avinabadalal on 06/03/18.
 * Bottom sheet alert dialog
 */

class BottomSheetAlertDialog(private val context: Context) {
    private var contentView: View? = null

    private var title: TextView? = null
    private var content: TextView? = null
    private var negativeButton: TextView? = null
    private var positiveButton: AppCompatButton? = null
    private var neutralButton: AppCompatButton? = null

    private var bottomSheetDialog: BottomSheetDialog? = null

    init {

        init()
    }

    private fun init() {
        contentView = View.inflate(context, R.layout.bottom_sheet_msg, null)

        title = contentView!!.findViewById(R.id.title)
        content = contentView!!.findViewById(R.id.content)
        negativeButton = contentView!!.findViewById(R.id.negativeButton)
        positiveButton = contentView!!.findViewById(R.id.positiveButton)
        neutralButton = contentView!!.findViewById(R.id.neutralButton)

        bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogDark)
    }

    fun setTitle(@StringRes iTitle: Int): BottomSheetAlertDialog {
        title!!.setText(iTitle)

        return this
    }

    fun setTitle(sTitle: String): BottomSheetAlertDialog {
        title!!.text = sTitle

        return this
    }

    fun setMessage(@StringRes iTitle: Int): BottomSheetAlertDialog {
        content!!.setText(iTitle)

        return this
    }

    fun setMessage(sTitle: String): BottomSheetAlertDialog {
        content!!.text = sTitle

        return this
    }

    fun setPositiveButton(@StringRes iTitle: Int, onClickListener: View.OnClickListener): BottomSheetAlertDialog {
        positiveButton!!.visibility = View.VISIBLE
        positiveButton!!.setText(iTitle)
        positiveButton!!.setOnClickListener { view ->
            onClickListener.onClick(view)
            bottomSheetDialog!!.dismiss()
        }

        return this
    }

    fun setNegativeButton(@StringRes iTitle: Int, onClickListener: View.OnClickListener): BottomSheetAlertDialog {
        negativeButton!!.visibility = View.VISIBLE
        negativeButton!!.setText(iTitle)
        negativeButton!!.setOnClickListener { view ->
            onClickListener.onClick(view)
            bottomSheetDialog!!.dismiss()
        }

        return this
    }

    fun setNeutralButton(@StringRes iTitle: Int, onClickListener: View.OnClickListener): BottomSheetAlertDialog {
        neutralButton!!.visibility = View.VISIBLE
        neutralButton!!.setText(iTitle)
        neutralButton!!.setOnClickListener { view ->
            onClickListener.onClick(view)
            bottomSheetDialog!!.dismiss()
        }

        return this
    }

    fun setCancelable(b: Boolean): BottomSheetAlertDialog {
        bottomSheetDialog!!.setCancelable(b)

        return this
    }

    fun show(): Boolean {
        bottomSheetDialog!!.setContentView(contentView)
        bottomSheetDialog!!.show()

        return true
    }
}