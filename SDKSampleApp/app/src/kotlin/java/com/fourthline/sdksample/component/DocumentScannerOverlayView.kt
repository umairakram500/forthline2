package com.fourthline.sdksample.component

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.fourthline.core.DocumentType
import com.fourthline.sdksample.*
import com.fourthline.sdksample.databinding.OverlayDocumentBinding
import com.fourthline.sdksample.helper.documentscanner.*
import com.fourthline.vision.document.*
import kotlinx.android.synthetic.main.overlay_document.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DocumentScannerOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private val binding = OverlayDocumentBinding.inflate(LayoutInflater.from(context), this)

    init {
        onLayoutMeasuredOnce { redrawPunchhole() }
    }

    /**
     * Job for assistive UI in case automatic scanning does not succeed.
     */
    private var manualScannerActivationJob: Job? = null

    /**
     * Flag to control whether result per step are shown for confirmation to the end-user.
     */
    var showIntermediateResults = true

    /**
     * Configure the UI elements for this View to act on the methods of [DocumentScanner]
     */
    fun configureButtons(documentScanner: DocumentScanner, onCloseScanner:() -> Unit) {
        with (binding) {
            buttonConfirm.setOnClickListener {
                context.vibrate()
                documentScanner.moveToNextStep()
            }

            buttonRetake.setOnClickListener {
                context.vibrate()
                documentScanner.resetCurrentStep()
            }

            buttonSnapshot.setOnClickListener {
                // Specifically don't apply vibration effect as this may cause blurriness.
                documentScanner.takeSnapshot()
            }

            buttonClose.setOnClickListener {
                context.vibrate()
                onCloseScanner()
            }
        }
    }

    fun getDocumentDetectionArea() = with(binding.documentMask) {
        Rect(left, top, right, bottom)
    }

    /**
     * Handles warning received from [DocumentScannerCallback] in implementing class.
     */
    fun onStepUpdate(step: DocumentScannerStep, documentType: DocumentType) {
        binding.stepLabel.setText(step.getInstructionLabel())
        binding.titleLabel.setText(step.getTitleLabel())

        val documentMaskResource = step.getDocumentMask(documentType)
        binding.documentMask.setImageDrawable(ContextCompat.getDrawable(context, documentMaskResource))

        redrawPunchhole()

        binding.scanPreviewContainer.hide()
        syncSnapshotButton(step)
    }

    /**
     * Handles warning received from [DocumentScannerCallback] in implementing class.
     */
    fun onWarnings(warnings: List<DocumentScannerStepWarning>) {
        binding.warningsLabel.setText(warnings
            .sortByPriority()
            .first()
            .getWarningLabel())

        findViewTreeLifecycleOwner()?.run {
            lifecycleScope.scheduleCleanup(binding.warningsLabel)
        }
    }

    /**
     * If configured, this overlays the view with a summary of the current
     * [DocumentScannerStepResult]. The user is asked to confirm this or try this step again.
     */
    fun showIntermediateResult(result: DocumentScannerStepResult) : Boolean {
        if (showIntermediateResults) {
            with (binding){
                scanPreview.setImageBitmap(result.image.cropped)
                scanPreviewLabel.setText(result.getConfirmationLabel())

                scanPreviewContainer.show()
            }
        }

        return showIntermediateResults
    }

    /**
     * Shows or Hides Snapshot button for the camera based on the current step configuration.
     *
     * As a best practice it is recommended to show a snapshot button for automatic detection steps,
     * after a delay. This allows the end-user to proceed where automatic scanning takes longer
     * than expected.
     */
    private fun syncSnapshotButton(step: DocumentScannerStep) {
        if (step.isAutoDetectAvailable) {
            binding.buttonSnapshot.hide()

            findViewTreeLifecycleOwner()?.run {
                // Show snapshot button after given delay
                manualScannerActivationJob?.cancel()
                manualScannerActivationJob = lifecycleScope.launch(Dispatchers.Main) {
                        delay(TimeUnit.SECONDS.toMillis(15))
                        binding.titleLabel.setText(R.string.document_scanner_status_manual_mode_explanation)
                        binding.buttonSnapshot.show()
                    }
            }
        } else binding.buttonSnapshot.show()
    }

    /**
     * On View layout changes, this updates the Punchhole to provide a cutout matching the given
     * Document Detection Area, accounting for new view bounds.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        redrawPunchhole()
    }

    /**
     * Redraws the Punchhole, matching the Document Detection Area.
     */
    private fun redrawPunchhole() = with(binding.punchhole) {
        punchholeRect = getDocumentDetectionArea()
        postInvalidate()
    }
}