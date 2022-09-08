/**
 * Copyright © 2022 Safened - Fourthline B.V. All rights reserved.
 */
package com.fourthline.sdksample

import android.Manifest
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fourthline.core.location.LocationProvider
import com.fourthline.core.location.LocationProviderError

interface PermissionsHelper {
    fun requestPermissionsForSelfie(
        activity: AppCompatActivity,
        success: () -> Unit,
        failure: (description: String) -> Unit
    )

    fun requestPermissionsForDocument(
        activity: AppCompatActivity,
        success: () -> Unit,
        failure: (description: String) -> Unit
    )

    fun requestLocationPermissions(activity: AppCompatActivity, callback: () -> Unit)
    fun handlePermissionError(activity: AppCompatActivity, error: LocationProviderError)
}

class PermissionsHelperImpl(private val locationProvider: LocationProvider) : PermissionsHelper {

    override fun requestPermissionsForSelfie(
        activity: AppCompatActivity,
        success: () -> Unit,
        failure: (description: String) -> Unit
    ) {
        requestScannerPermissions(activity, success, failure)
    }

    override fun requestPermissionsForDocument(
        activity: AppCompatActivity,
        success: () -> Unit,
        failure: (description: String) -> Unit
    ) {
        requestScannerPermissions(activity, success, failure)
    }

    private fun requestScannerPermissions(
        activity: AppCompatActivity,
        success: () -> Unit,
        failure: (description: String) -> Unit
    ) {
        val getPermission = activity.activityResultRegistry.register(
            REGISTRY_KEY,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                locationProvider.requestAuthorization({
                    success()
                }, {
                    handlePermissionError(activity, it)
                })
            } else {
                val notGrantedPermissions = permissions.filter { !it.value }.map { it.key + "\n" }
                failure("Permissions $notGrantedPermissions are not granted")
            }
        }
        getPermission.launch(
            (arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ))
        )
    }

    override fun requestLocationPermissions(activity: AppCompatActivity, callback: () -> Unit) {
        locationProvider.requestAuthorization({
            callback()
        }, {
            handlePermissionError(activity, it)
        })
    }

    override fun handlePermissionError(activity: AppCompatActivity, error: LocationProviderError) {
        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
    }

    private companion object {
        const val REGISTRY_KEY = "REGISTRY_KEY"
    }
}