package com.fourthline.sdksample.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.fourthline.sdksample.R
import com.fourthline.sdksample.databinding.ViewNfcScannerStepBinding
import com.fourthline.sdksample.hide
import com.fourthline.sdksample.show
import kotlinx.android.synthetic.main.view_nfc_scanner_step.view.*

class CommonStatusStepView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private var binding = ViewNfcScannerStepBinding.inflate(LayoutInflater.from(context), this)

    fun setLabel(value: CharSequence) {
        stepLabel.text = value
    }

    fun setStatus(status: UiStepStatus) {
        when (status) {
            UiStepStatus.IN_PROGRESS -> {
                binding.statusProgressBar.show()
                binding.statusImageView.hide()
            }
            UiStepStatus.SUCCESS -> {
                binding.statusProgressBar.hide()
                binding.statusImageView.show()
                binding.statusImageView.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_nfc_step_status_success)
                )
            }
            UiStepStatus.FAILURE -> {
                binding.statusProgressBar.hide()
                binding.statusImageView.show()
                binding.statusImageView.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_nfc_step_status_failure)
                )
            }
        }

    }

    enum class UiStepStatus {
        IN_PROGRESS, SUCCESS, FAILURE
    }
}
