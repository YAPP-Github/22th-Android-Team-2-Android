package com.yapp.bol.presentation.view.group.join

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import com.yapp.bol.presentation.R
import com.yapp.bol.presentation.databinding.DialogInputBinding
import com.yapp.bol.presentation.utils.Keyboard
import com.yapp.bol.presentation.utils.dpToPx
import com.yapp.bol.presentation.utils.inflate

class InputDialog(
    context: Context,
) : Dialog(context) {

    private val binding: DialogInputBinding by lazy {
        context.inflate(R.layout.dialog_input, null, false)
    }

    private var onLimitExceeded: ((String) -> Unit)? = null
    private var onLimit: Int = 0

    init {
        setContentView(binding.root)

        window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
        binding.root.updateLayoutParams<MarginLayoutParams> {
            leftMargin = context.dpToPx(16)
            rightMargin = context.dpToPx(16)
            bottomMargin = context.dpToPx(36)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPress()

        binding.etInput.requestFocus()

        binding.etInput.doAfterTextChanged { text ->

            binding.tvInputCount.text = "${text?.length ?: 0}/$onLimit"

            if ((text?.length ?: 0) > onLimit) {
                onLimitExceeded?.invoke(text.toString())
                binding.etInput.setText(text?.substring(0, onLimit))
                binding.etInput.setSelection(onLimit)
            }
        }
    }

    fun setOnSummit(onSummit: (String) -> Unit): InputDialog {
        binding.summitLayout.visibility = View.VISIBLE
        binding.etLayout.updatePadding(bottom = 0)

        binding.tvSummit.setOnClickListener {
            onSummit(binding.etInput.text.toString())
            dismiss()
        }
        return this
    }

    fun setTitle(title: CharSequence): InputDialog {
        binding.tvTitle.text = title
        return this
    }

    fun setTitle(@StringRes title: Int?): InputDialog {
        binding.tvTitle.text = context.getString(title ?: return this)
        return this
    }

    fun setMessage(message: CharSequence): InputDialog {
        binding.tvMessage.text = message
        return this
    }

    fun setMessage(@StringRes message: Int): InputDialog {
        binding.tvMessage.text = context.getString(message)
        return this
    }

    fun setLimitSize(limit: Int): InputDialog {
        this.onLimit = limit
        return this
    }

    fun setOnLimit(onLimitExceeded: (String) -> Unit): InputDialog {
        this.onLimitExceeded = onLimitExceeded
        return this
    }

    private fun onBackPress() {
        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                Keyboard.close(context, currentFocus ?: return@setOnKeyListener true)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }
}
