/*
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.fourthline.core.mrz.MrzInfo;
import com.fourthline.nfc.NfcData;
import com.fourthline.nfc.NfcScannerActivity;
import com.fourthline.nfc.NfcScannerConfig;
import com.fourthline.nfc.NfcScannerError;
import com.fourthline.nfc.NfcScannerResult;
import com.fourthline.nfc.NfcScannerSecurityKey;
import com.fourthline.nfc.NfcScannerStep;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;
import static com.fourthline.sdksample.NfcResultActivity.KEY_NFC_MRZ;
import static com.fourthline.sdksample.NfcResultActivity.KEY_NFC_PHOTO;
import static com.fourthline.sdksample.Utils.asString;

public class NfcActivity extends NfcScannerActivity {

    private static final String TAG = NfcActivity.class.getSimpleName();

    private TextView stepLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        stepLabel = findViewById(R.id.stepLabel);
        findViewById(R.id.buttonClose).setOnClickListener((view) -> {
            Utils.vibrate(this);
            finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        stepLabel.setText(R.string.nfc_scanner_description);
    }

    @Override
    public void onFail(@NotNull NfcScannerError error) {
        Utils.vibrate(this);
        Log.d(TAG, "Scanner failed with error: " + error);

        runOnUiThread(() -> Toast.makeText(this, asString(error), LENGTH_LONG).show());

        finish();
    }

    @Override
    public void onStepUpdate(@NotNull NfcScannerStep step) {
        Utils.vibrate(this);
        Log.d(TAG, "Step update: " + step);

        runOnUiThread(() -> stepLabel.setText(asString(step)));
    }

    @Override
    public void onSuccess(@NotNull NfcScannerResult result) {
        Utils.vibrate(this);
        Log.d(TAG, "NFC scan succeed");

        Bundle data = new Bundle();

        MrzInfo mrz = result.getData(NfcData.NfcDataType.MRZ_INFO);
        String rawMrz = null;
        if (mrz != null) rawMrz = mrz.getRawMrz();

        data.putString(KEY_NFC_MRZ, rawMrz);

        Bitmap photo = result.getData(NfcData.NfcDataType.PHOTO);
        if (photo != null) data.putByteArray(KEY_NFC_PHOTO, Utils.toByteArray(photo));

        NfcResultActivity.start(this, data);

        finish();
    }

    @NotNull
    @Override
    public NfcScannerConfig getConfig() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(2000, 0, 1);
        Date birthDate = calendar.getTime();

        calendar.set(2030, 0, 1);
        Date expiryDate = calendar.getTime();

        return new NfcScannerConfig(NfcScannerSecurityKey.createWithMrtdData(
                "ABC123456",
                birthDate,
                expiryDate
        ));
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, NfcActivity.class);
        context.startActivity(intent);
    }
}
