/*
  Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample;

import static android.widget.Toast.LENGTH_LONG;
import static androidx.lifecycle.LifecycleOwnerKt.getLifecycleScope;
import static com.fourthline.core.DocumentType.ID_CARD;
import static com.fourthline.sdksample.DocumentResultActivity.KEY_DOCUMENT_MRZ;
import static com.fourthline.sdksample.DocumentResultActivity.KEY_DOCUMENT_VIDEO_URI;
import static com.fourthline.sdksample.Utils.asString;
import static com.fourthline.sdksample.Utils.findMaskDrawable;
import static com.fourthline.sdksample.Utils.hide;
import static com.fourthline.sdksample.Utils.prettify;
import static com.fourthline.sdksample.Utils.show;
import static com.fourthline.vision.document.MrzValidationPolicy.STRONG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fourthline.core.mrz.MrzInfo;
import com.fourthline.vision.RecordingType;
import com.fourthline.vision.document.DocumentScannerActivity;
import com.fourthline.vision.document.DocumentScannerConfig;
import com.fourthline.vision.document.DocumentScannerError;
import com.fourthline.vision.document.DocumentScannerResult;
import com.fourthline.vision.document.DocumentScannerStep;
import com.fourthline.vision.document.DocumentScannerStepError;
import com.fourthline.vision.document.DocumentScannerStepResult;
import com.fourthline.vision.document.DocumentScannerStepWarning;
import com.fourthline.vision.document.DocumentValidationConfig;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;

public class DocumentActivity extends DocumentScannerActivity {

    private static final String TAG = DocumentActivity.class.getSimpleName();

    private static boolean SHOW_INTERMEDIATE_RESULTS = true;

    private View overlayView;
    private PunchholeView punchhole;
    private ImageView documentMask;
    private TextView stepLabel;
    private TextView warningsLabel;
    private View buttonSnapshot;
    private ImageView scanPreview;
    private View buttonRetake;
    private View buttonConfirm;

    @NotNull
    @Override
    public DocumentScannerConfig getConfig() {
        return new DocumentScannerConfig(
                ID_CARD,
                true, // includeAngledSteps
                false, // debugModeEnabled
                RecordingType.VIDEO_ONLY,
                STRONG,
                new DocumentValidationConfig(
                        18 // minAge
                )
        );
    }

    @NotNull
    @Override
    public Rect getDocumentDetectionArea() {
        return new Rect(
                documentMask.getLeft(),
                documentMask.getTop(),
                documentMask.getRight(),
                documentMask.getBottom()
        );
    }

    @Nullable
    @Override
    public View getOverlayView() {
        overlayView = LayoutInflater.from(this)
                .inflate(R.layout.overlay_document, findViewById(android.R.id.content), false);

        return overlayView;
    }

    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

        punchhole = overlayView.findViewById(R.id.punchhole);
        documentMask = overlayView.findViewById(R.id.documentMask);
        stepLabel = overlayView.findViewById(R.id.stepLabel);
        warningsLabel = overlayView.findViewById(R.id.warningsLabel);
        buttonSnapshot = overlayView.findViewById(R.id.buttonSnapshot);
        scanPreview = overlayView.findViewById(R.id.scanPreview);
        buttonRetake = overlayView.findViewById(R.id.buttonRetake);
        buttonConfirm = overlayView.findViewById(R.id.buttonConfirm);

        buttonSnapshot.setOnClickListener(v -> {
            Utils.vibrate(this);
            takeSnapshot();
        });

        buttonConfirm.setOnClickListener(v -> {
            Utils.vibrate(this);
            moveToNextStep();
        });

        buttonRetake.setOnClickListener(v -> {
            Utils.vibrate(this);
            resetCurrentStep();
        });

        overlayView.findViewById(R.id.buttonClose).setOnClickListener((view) -> {
            Utils.vibrate(this);
            finish();
        });

        Utils.onLayoutMeasuredOnce(getWindow().findViewById(android.R.id.content), (view) -> {
            punchhole.setPunchholeRect(getDocumentDetectionArea());
            punchhole.postInvalidate();
            return null;
        });
    }

    @Override
    public void onStepsCountUpdate(@NotNull int count) {
        Log.d(TAG, "Document step count has been updated to $count")
    }

    @Override
    public void onStepUpdate(@NotNull DocumentScannerStep step) {
        Utils.vibrate(this);
        Log.d(TAG, "Step update: " + prettify(step));

        runOnUiThread(() -> {
            stepLabel.setText(asString(step));

            documentMask.setImageDrawable(findMaskDrawable(step, this));

            punchhole.setPunchholeRect(getDocumentDetectionArea());
            punchhole.postInvalidate();

            syncUi(UiState.SCANNING);


// You can add code to show snapshot button after some delay, e.g 5-15 seconds, as step
// timeout is set to 30 seconds.
            if (step.isAutoDetectAvailable()) hide(buttonSnapshot);
            else show(buttonSnapshot);
        });
    }

    @Override
    public void onWarnings(@NotNull List<? extends DocumentScannerStepWarning> warnings) {
        Log.d(TAG, "Current warnings: " + prettify(warnings));

        runOnUiThread(() -> warningsLabel.setText(asString(warnings)));

        Utils.scheduleCleanup(getLifecycleScope(this), warningsLabel);
    }

    @Override
    public void onStepFail(@NotNull DocumentScannerStepError error) {
        Utils.vibrate(this);
        Log.d(TAG, "Scanner misuse, reason: " + error);
        throw new RuntimeException("Business logic violation, should never happen");
    }

    @Override
    public void onStepSuccess(@NotNull DocumentScannerStepResult result) {
        Utils.vibrate(this);
        Log.d(TAG, "Step scan succeed");

        if (SHOW_INTERMEDIATE_RESULTS)
            runOnUiThread(() -> showIntermediateResult(result.getImage().getCropped()));
        else moveToNextStep();
    }

    @Override
    public void onFail(@NotNull DocumentScannerError error) {
        Utils.vibrate(this);
        Log.d(TAG, "Scanner failed with error: " + error);

        runOnUiThread(() -> Toast.makeText(this, asString(error), LENGTH_LONG).show());

        finish();
    }

    @Override
    public void onSuccess(@NotNull DocumentScannerResult result) {
        Utils.vibrate(this);
        Log.d(TAG, "Scan successful");

        Bundle data = new Bundle();
        URI videoUri = result.getVideoUrl();
        data.putString(KEY_DOCUMENT_VIDEO_URI, videoUri != null ? videoUri.getPath() : "n/a");
        MrzInfo mrzInfo = result.getMrzInfo();
        data.putString(KEY_DOCUMENT_MRZ, mrzInfo != null ? mrzInfo.getRawMrz() : "n/a");

        DocumentResultActivity.start(this, data);

        finish();
    }

    private void showIntermediateResult(Bitmap image) {
        scanPreview.setImageBitmap(image);
        syncUi(UiState.INTERMEDIATE_RESULT);
    }

    private void syncUi(UiState newState) {
        switch (newState) {
            case SCANNING:
                show(stepLabel);
                show(warningsLabel);
                show(buttonSnapshot);
                hide(scanPreview);
                hide(buttonRetake);
                hide(buttonConfirm);
                break;
            case INTERMEDIATE_RESULT:
                stepLabel.setText("");
                warningsLabel.setText("");
                hide(stepLabel);
                hide(warningsLabel);
                hide(buttonSnapshot);
                show(scanPreview);
                show(buttonRetake);
                show(buttonConfirm);
                break;
        }
    }

    private enum UiState {SCANNING, INTERMEDIATE_RESULT}

    public static void start(Context context) {
        Intent intent = new Intent(context, DocumentActivity.class);
        context.startActivity(intent);
    }
}