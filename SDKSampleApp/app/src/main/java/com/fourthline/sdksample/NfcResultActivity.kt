/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fourthline.core.mrz.MrzInfo
import com.fourthline.nfc.NfcData
import kotlinx.android.synthetic.main.activity_nfc_result.*

class NfcResultActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfc_result)

        // Handle results available on the KycResultContainer
        KycResultContainer.nfcResult?.let {
            nfcMrz.text = "MRZ Info:\n ${it.getData<MrzInfo>(NfcData.NfcDataType.MRZ_INFO)?.rawMrz}"
            nfcImage.setImageBitmap(it.getData<Bitmap>(NfcData.NfcDataType.PHOTO))
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) = with(context) {
            Intent(this, NfcResultActivity::class.java)
                .let(::startActivity)
        }
    }
}