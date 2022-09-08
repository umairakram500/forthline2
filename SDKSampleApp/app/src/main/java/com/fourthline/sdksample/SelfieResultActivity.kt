/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fourthline.sdksample.activities.SelfieActivity
import kotlinx.android.synthetic.main.activity_selfie_result.*

class SelfieResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selfie_result)

        // Access preview of Selfie from saved SelfieScannerResult.
        val preview = KycResultContainer.selfieResult?.image?.cropped
        selfiePreview.setImageBitmap(preview)

        buttonRetake.setOnClickListener {
            SelfieActivity.start(this)
            finish()
        }

        buttonConfirm.setOnClickListener {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) = with(context) {
            Intent(this, SelfieResultActivity::class.java)
                .let(::startActivity)
        }
    }
}