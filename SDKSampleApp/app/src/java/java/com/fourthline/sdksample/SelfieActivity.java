/*
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample;

import static android.widget.Toast.LENGTH_LONG;
import static androidx.lifecycle.LifecycleOwnerKt.getLifecycleScope;
import static com.fourthline.sdksample.SelfieResultActivity.KEY_SELFIE_VIDEO_URI;
import static com.fourthline.sdksample.Utils.asString;
import static com.fourthline.vision.selfie.LivenessCheckType.HEAD_TURN;
import static kotlin.collections.CollectionsKt.joinToString;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fourthline.vision.RecordingType;
import com.fourthline.vision.selfie.SelfieScannerActivity;
import com.fourthline.vision.selfie.SelfieScannerConfig;
import com.fourthline.vision.selfie.SelfieScannerError;
import com.fourthline.vision.selfie.SelfieScannerResult;
import com.fourthline.vision.selfie.SelfieScannerStep;
import com.fourthline.vision.selfie.SelfieScannerWarning;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;

public class SelfieActivity extends SelfieScannerActivity {

    private static final String TAG = SelfieActivity.class.getSimpleName();

    private View overlayView;
    private View selfieMask;
    private PunchholeView punchhole;
    private TextView stepLabel;
    private TextView warningsLabel;

    @NotNull
    @Override
    public SelfieScannerConfig getConfig() {
        return new SelfieScannerConfig(
                false, // debugModeEnabled
                RecordingType.VIDEO_ONLY,
                HEAD_TURN
        );
    }

    @Nullable
    @Override
    public View getOverlayView() {
        overlayView = LayoutInflater.from(this).inflate(
                R.layout.overlay_selfie,
                findViewById(android.R.id.content),
                false);

        return overlayView;
    }

    @NotNull
    @Override
    public Rect getFaceDetectionArea() {
        return new Rect(selfieMask.getLeft(),
                selfieMask.getTop(),
                selfieMask.getRight(),
                selfieMask.getBottom()
        );
    }


    @Override
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);

        selfieMask = overlayView.findViewById(R.id.selfieMask);
        punchhole = overlayView.findViewById(R.id.punchhole);
        stepLabel = overlayView.findViewById(R.id.stepLabel);
        warningsLabel = overlayView.findViewById(R.id.warningsLabel);
        overlayView.findViewById(R.id.buttonClose).setOnClickListener((view) -> {
            Utils.vibrate(this);
            finish();
        });

        Utils.onLayoutMeasuredOnce(getWindow().findViewById(android.R.id.content), (view) -> {
            punchhole.setPunchholeRect(getFaceDetectionArea());
            punchhole.postInvalidate();
            return null;
        });
    }

    /**
     * You may enter {@link SelfieScannerStep#MANUAL_SELFIE} step when {@link SelfieScannerConfig#getIncludeManualSelfiePolicy()}
     * is set to TRUE and selfie step timeouts.
     * In this case present snapshot button to user and call {@link com.fourthline.vision.selfie.SelfieScanner#takeSnapshot} on click.
     */
    @Override
    public void onStepUpdate(@NotNull SelfieScannerStep step) {
        Utils.vibrate(this);
        Log.d(TAG, "Step update: " + step);

        runOnUiThread(() -> stepLabel.setText(asString(step)));
    }

    @Override
    public void onWarnings(@NotNull List<? extends SelfieScannerWarning> warnings) {
        String prettyWarnings = joinToString(warnings, ", ", "[ ", " ]", -1, "...", null);
        Log.d(TAG, "Current warnings: " + prettyWarnings);

        runOnUiThread(() -> warningsLabel.setText(asString(warnings.get(0))));

        Utils.scheduleCleanup(getLifecycleScope(this), warningsLabel);
    }

    @Override
    public void onFail(@NotNull SelfieScannerError error) {
        Utils.vibrate(this);
        Log.d(TAG, "Scanner failed with error: " + error);

        runOnUiThread(() -> Toast.makeText(this, asString(error), LENGTH_LONG).show());

        finish();
    }

    @Override
    public void onSuccess(@NotNull SelfieScannerResult result) {
        Utils.vibrate(this);
        Log.d(TAG, "Selfie scan succeed");

        Bundle data = new Bundle();
        URI videoUri = result.getVideoUrl();
        data.putString(KEY_SELFIE_VIDEO_URI, videoUri != null ? videoUri.getPath() : "n/a");

        SelfieResultActivity.start(this, data, result.getImage().getCropped());

        finish();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SelfieActivity.class);
        context.startActivity(intent);
    }
}