package com.example.activos360.ui.Screens/*package com.example.activos360.ui.Screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@Composable
fun ScanCamera(
    onScanned: (String) -> Unit,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var granted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!granted) {
            Text("Se requiere permiso de cámara", modifier = Modifier.align(Alignment.Center))
        } else {
            CameraPreviewSimple(ctx, lifecycleOwner, onDetected = { code -> onScanned(code) }, onError = { onClose() })
        }

        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel), contentDescription = "Cerrar")
        }
    }
}

@Composable
private fun CameraPreviewSimple(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onDetected: (String) -> Unit,
    onError: (Throwable) -> Unit
) {
    val executor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) { onDispose { executor.shutdown() } }

    AndroidView(factory = { ctx ->
        val pv = PreviewView(ctx)
        val providerF = ProcessCameraProvider.getInstance(ctx)
        providerF.addListener({
            try {
                val cameraProvider = providerF.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(pv.surfaceProvider) }
                val analysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
                val scanner = BarcodeScanning.getClient()
                var found = false

                analysis.setAnalyzer(executor) { proxy ->
                    val media = proxy.image
                    if (media != null && !found) {
                        val image = InputImage.fromMediaImage(media, proxy.imageInfo.rotationDegrees)
                        scanner.process(image)
                            .addOnSuccessListener { codes ->
                                if (!found && codes.isNotEmpty()) {
                                    val code = codes.first().rawValue ?: codes.first().displayValue
                                    if (!code.isNullOrEmpty()) {
                                        found = true
                                        onDetected(code)
                                    }
                                }
                            }
                            .addOnFailureListener { onError(it) }
                            .addOnCompleteListener { proxy.close() }
                    } else proxy.close()
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
            } catch (e: Exception) { onError(e) }
        }, ContextCompat.getMainExecutor(ctx))
        pv
    }, modifier = Modifier.fillMaxSize())
}

 */