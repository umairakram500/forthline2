/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
@file:JvmName("Utils")

package com.fourthline.sdksample

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Vibrator
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fourthline.core.DocumentFileSide.*
import com.fourthline.kyc.zipper.ZipperError
import com.fourthline.kyc.zipper.ZipperError.*
import com.fourthline.nfc.NfcScannerError
import com.fourthline.nfc.NfcScannerError.*
import com.fourthline.nfc.NfcScannerStep
import com.fourthline.nfc.NfcScannerStep.*
import com.fourthline.vision.selfie.SelfieScannerError.*
import com.fourthline.vision.selfie.SelfieScannerStep.*
import com.fourthline.vision.selfie.SelfieScannerWarning.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
fun Context.vibrate() =
    (getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator).vibrate(100)


/**
 * Convenience method to configure an AlertDialog for display on an Activity.
 */
fun Activity.showDialog(
    title: CharSequence,
    message: CharSequence,
    positiveButtonText: CharSequence,
    positiveButtonHandler: () -> Unit? = { },
    negativeButtonText: CharSequence? = null,
    negativeButtonHandler: () -> Unit? = { }
): AlertDialog {
    val builder = AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton(positiveButtonText) { _, _ ->
            positiveButtonHandler.invoke()
        }
        if (negativeButtonText != null) {
            setNegativeButton(negativeButtonText) { _, _ ->
                negativeButtonHandler.invoke()
            }
        }
    }

    return builder.show()
}

fun View.hide() {
    visibility = GONE
}

fun View.show() {
    visibility = VISIBLE
}

fun View.onLayoutMeasuredOnce(action: (View) -> Unit) = viewTreeObserver.addOnGlobalLayoutListener(
    object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)

            action(this@onLayoutMeasuredOnce)
        }
    })


private var cleanupJob: Job? = null
fun CoroutineScope.scheduleCleanup(target: TextView) {
    cleanupJob?.cancel()
    cleanupJob = launch(Dispatchers.Main) {
        delay(500)
        target.text = ""
    }
}