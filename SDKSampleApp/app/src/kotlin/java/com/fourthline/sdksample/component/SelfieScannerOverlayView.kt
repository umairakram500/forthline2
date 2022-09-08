package com.fourthline.sdksample.component

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.fourthline.sdksample.*
import com.fourthline.sdksample.databinding.OverlaySelfieBinding
import com.fourthline.sdksample.helper.selfiescanner.getInstructionLabel
import com.fourthline.sdksample.helper.selfiescanner.getWarningLabel
import com.fourthline.sdksample.helper.selfiescanner.sortByPriority
import com.fourthline.vision.selfie.SelfieScanner
import com.fourthline.vision.selfie.SelfieScannerStep
import com.fourthline.vision.selfie.SelfieScannerWarning

class SelfieScannerOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private val binding = OverlaySelfieBinding.inflate(LayoutInflater.from(context), this)

    init {
        onLayoutMeasuredOnce { redrawPunchhole() }
    }

    /**
     * Flag to control whether result per step are shown for confirmation to the end-user.
     */
    var showIntermediateResults = true

    /**
     * Configure the UI elements for this View to act on the methods of [SelfieScanner]
     */
    fun configureButtons(onCloseScanner:() -> Unit) {
        binding.buttonClose.setOnClickListener {
                context.vibrate()
                onCloseScanner()
            }
    }

    fun getFaceDetectionArea() = with(binding.selfieMask) {
        Rect(left, top, right, bottom)
    }

    /**
     * Handles warning received from [SelfieScanner] in implementing class.
     */
    fun onStepUpdate(step: SelfieScannerStep) {
        binding.stepLabel.setText(step.getInstructionLabel())
    }

    /**
     * Handles warning received from [com.fourthline.vision.selfie.SelfieScannerCallback] in implementing class.
     */
    fun onWarnings(warnings: List<SelfieScannerWarning>) {
        binding.warningsLabel.setText(warnings
            .sortByPriority()
            .first()
            .getWarningLabel())

        findViewTreeLifecycleOwner()?.run {
            lifecycleScope.scheduleCleanup(binding.warningsLabel)
        }
    }

    /**
     * On View layout changes, this updates the Punchhole to provide a cutout matching the given
     * Face Detection Area, accounting for new view bounds.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        redrawPunchhole()
    }

    /**
     * Redraws the Punchhole, matching the Document Detection Area.
     */
    private fun redrawPunchhole() = with(binding.punchhole) {
        punchholeRect = getFaceDetectionArea()
        postInvalidate()
    }
}