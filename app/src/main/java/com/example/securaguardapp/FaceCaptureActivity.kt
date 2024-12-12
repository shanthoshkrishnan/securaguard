package com.example.securaguardapp

import com.example.securaguardapp.KeyManager
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.securaguardapp.FaceComparisonHelper
import android.content.Intent

class FaceCaptureActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isUsingFrontCamera = true
    private lateinit var keyManager: KeyManager
    private lateinit var faceComparisonHelper: FaceComparisonHelper

    companion object {
        private const val CAMERA_REQUEST_CODE = 101
        private const val PHONE_STATE_REQUEST_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_capture)

        // Handle the intent
        val intent = intent
        val data: Uri? = intent.data
        if (data != null) {
            val userId = data.getQueryParameter("userId")
            Log.d("FaceCaptureActivity", "User ID: $userId")
            // Use this data to personalize the app experience
        }

        requestPhoneStatePermission() // Request phone state permission

        previewView = findViewById(R.id.previewView)
        val switchCameraButton: Button = findViewById(R.id.switchCameraButton)
        val captureButton: Button = findViewById(R.id.captureButton)

        keyManager = KeyManager(this) // Initialize KeyManager
        faceComparisonHelper = FaceComparisonHelper(this) // Initialize FaceComparisonHelper
        requestCameraPermission()
        cameraExecutor = Executors.newSingleThreadExecutor()

        switchCameraButton.setOnClickListener {
            isUsingFrontCamera = !isUsingFrontCamera
            startCamera()
        }

        captureButton.setOnClickListener {
            captureAndCompareImage()
        }

        startCamera()
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    private fun requestPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                PHONE_STATE_REQUEST_CODE
            )
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindPreview()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        val cameraSelector = if (isUsingFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        cameraProvider?.unbindAll()
        cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    }

    private fun captureAndCompareImage() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(byteArrayOutputStream).build()

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val imageByteArray = byteArrayOutputStream.toByteArray()

                // Perform face comparison using FaceComparisonHelper
                val comparisonResult = faceComparisonHelper.compareFace(imageByteArray)

                if (comparisonResult) {
                    Log.d("FaceCaptureActivity", "Face comparison succeeded")
                    val encryptedResult = faceComparisonHelper.encryptResultWithIMEI(imageByteArray)
                    Log.d("FaceCaptureActivity", "Encrypted result: $encryptedResult")
                } else {
                    Log.e("FaceCaptureActivity", "Face comparison failed")
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("FaceCaptureActivity", "Image capture failed: ${exception.message}", exception)
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PHONE_STATE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("FaceCaptureActivity", "Phone state permission granted")
                } else {
                    Log.e("FaceCaptureActivity", "Phone state permission denied")
                }
            }
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Log.e("FaceCaptureActivity", "Camera permission denied")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
    }
}
