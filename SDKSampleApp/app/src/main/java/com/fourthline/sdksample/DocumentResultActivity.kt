/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_document_result.*

class DocumentResultActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_result)

        with(intent.extras!!) {
            documentVideoUri.text = "Document video URL:\n ${getString(KEY_DOCUMENT_VIDEO_URI)}"
            documentMrz.text = "MRZ Info:\n ${getString(KEY_DOCUMENT_MRZ)}"
        }
    }

    companion object {
        const val KEY_DOCUMENT_VIDEO_URI = "key_document_video_uri"
        const val KEY_DOCUMENT_MRZ = "key_document_mrz"

        @JvmStatic
        fun start(context: Context, data: Bundle) = with(context) {
            Intent(this, DocumentResultActivity::class.java)
                .apply { putExtras(data) }
                .let(::startActivity)
        }
    }
}