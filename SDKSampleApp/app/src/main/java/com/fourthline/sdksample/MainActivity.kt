/**
 * Copyright © 2020 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.fragment.app.Fragment
import com.fourthline.sdksample.activities.DocumentActivity
import com.fourthline.sdksample.activities.NfcActivity
import com.fourthline.sdksample.activities.SelfieActivity
import com.fourthline.sdksample.activities.SubmitKycActivity
import com.fourthline.sdksample.databinding.ActivityMainBinding
import com.fourthline.sdksample.fragments.DocumentFragment
import com.fourthline.sdksample.fragments.NfcFragment
import com.fourthline.sdksample.fragments.SelfieFragment
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Flag that controls whether to launch the Sample Fragments or Sample Activities.
 */
private const val RUN_SAMPLE_FRAGMENTS = true

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupActionBar()
        setupButtons()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupButtons() = with(binding) {
        buttonNfc.setOnClickListener { startNfcScanner() }
        buttonSelfie.setOnClickListener { startSelfieScanner() }
        buttonDocument.setOnClickListener { startDocumentScanner() }
        buttonKyc.setOnClickListener { startSubmitKyc() }
        buttonOrca.setOnClickListener{ startOrcaLauncher() }
    }

    private fun startOrcaLauncher() {
        val intent = Intent(this, OrcaSampleActivity::class.java)
        startActivity(intent)
    }

    /**
     * To start the SelfieScanner, first ensure the required permissions are granted.
     *
     * - android.Manifest.permission.CAMERA (required)
     * - android.Manifest.permission.AUDIO (required, if configured to record audio)
     * - android.Manifest.permission.ACCESS_FINE_LOCATION (optional)
     *
     * Afterwards, launch a custom subclass of
     * [com.fourthline.vision.selfie.SelfieScannerActivity] or
     * [com.fourthline.vision.selfie.SelfieScannerFragment].
     */
    private fun startSelfieScanner() = requirePermissions(
        requiredPermissions = arrayOf(CAMERA),
        optionalPermissions = arrayOf(ACCESS_FINE_LOCATION),
        requestCode = SELFIE_SCANNER_PERMISSION_REQUEST
    ) {
        if (RUN_SAMPLE_FRAGMENTS) {
            showFragment(SelfieFragment())
        } else {
            SelfieActivity.start(this)
        }
    }

    /**
     * To start the DocumentScanner, first ensure the required permissions are granted.
     *
     * - android.Manifest.permission.CAMERA (required)
     * - android.Manifest.permission.ACCESS_FINE_LOCATION (optional)
     *
     * Afterwards, launch a custom subclass of
     * [com.fourthline.vision.document.DocumentScannerActivity] or
     * [com.fourthline.vision.document.DocumentScannerFragment].
     */
    private fun startDocumentScanner() = requirePermissions(
        requiredPermissions = arrayOf(CAMERA),
        optionalPermissions = arrayOf(ACCESS_FINE_LOCATION),
        requestCode = DOCUMENT_SCANNER_PERMISSION_REQUEST
    ) {
        if (RUN_SAMPLE_FRAGMENTS) {
            showFragment(DocumentFragment())
        } else {
            DocumentActivity.start(this)
        }
    }

    /**
     * To start the NfcScanner, first ensure the required permissions are granted.
     *
     * - android.Manifest.permission.ACCESS_FINE_LOCATION (optional)
     *
     * Afterwards, launch a custom subclass of
     * [com.fourthline.nfc.NfcScannerActivity] or
     * [com.fourthline.nfc.NfcScannerFragment].
     */
    private fun startNfcScanner() = requirePermissions(
        requiredPermissions = arrayOf(ACCESS_FINE_LOCATION),
        requestCode = NFC_SCANNER_PERMISSION_REQUEST
    ) {
        if (RUN_SAMPLE_FRAGMENTS) {
            showFragment(NfcFragment())
        } else {
            NfcActivity.start(this)
        }
    }

    /**
     * After collecting all Kyc data through Scanner and Custom UI, submit it for verification.
     */
    private fun startSubmitKyc() {
        SubmitKycActivity.start(this)
    }

    /**
     * Convenience method to check permissions for the requested functionality.
     *
     * When implementing this in your app, please follow the recommended approach from Android.
     * https://developer.android.com/training/permissions/requesting
     *
     * In this app permissions for all features are handled by this Activity. In your app this will
     * likely be handled in different Classes, allowing for a more precise approach.
     */
    private fun requirePermissions(
        requiredPermissions: Array<String> = emptyArray(),
        optionalPermissions: Array<String> = emptyArray(),
        requestCode: Int,
        onPermissionsAvailable: () -> Unit
    ) {
        val requiresExtraPermissions = requiredPermissions.any {
            checkSelfPermission(this, it) != PERMISSION_GRANTED
        }

        if (requiresExtraPermissions) {
            requestPermissions(
                this,
                requiredPermissions+optionalPermissions,
                requestCode)
        } else {
            onPermissionsAvailable()
        }
    }

    /**
     * Handles grant results for requested permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // If one of the required permissions is not granted, show a dialog.
        permissions.forEachIndexed { index, permission ->
            when (permission) {
                CAMERA -> {
                    if (grantResults[index] != PERMISSION_GRANTED) {
                        showPermissionRequiredDialog(
                            requestCode,
                            getString(R.string.permisson_camera_missing)
                        )
                        return
                    }
                }
                ACCESS_FINE_LOCATION -> {
                    if (grantResults[index] != PERMISSION_GRANTED) {
                        showPermissionRequiredDialog(
                            requestCode,
                            getString(R.string.permisson_location_missing)
                        )
                        return
                    }
                }
            }
        }

        startAfterPermissionRequestHandled(requestCode)
    }

    /**
     * Attempts to start a chosen feature, after previously accepting or denying permissions.
     */
    private fun startAfterPermissionRequestHandled(requestCode: Int) {
        when (requestCode) {
            SELFIE_SCANNER_PERMISSION_REQUEST -> {
                startSelfieScanner()
            }
            DOCUMENT_SCANNER_PERMISSION_REQUEST -> {
                startDocumentScanner()
            }
            NFC_SCANNER_PERMISSION_REQUEST -> {
                startNfcScanner()
            }
        }
    }

    private fun showPermissionRequiredDialog(
        requestCode: Int,
        message: CharSequence
    ) = showDialog(
        title = getString(R.string.app_name),
        message = message,
        positiveButtonText = getString(R.string.common_try_again),
        positiveButtonHandler = { startAfterPermissionRequestHandled(requestCode) },
        negativeButtonText = getString(R.string.common_dismiss)
    )

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragmentContainer, fragment)
            .addToBackStack(fragment.tag)
            .commit()
    }

    private companion object {
        const val SELFIE_SCANNER_PERMISSION_REQUEST = 1
        const val DOCUMENT_SCANNER_PERMISSION_REQUEST = 2
        const val NFC_SCANNER_PERMISSION_REQUEST = 3
    }
}