package com.example.demoproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.demoproject.HelperClasses.GraphicOverlay
import com.example.demoproject.HelperClasses.VisionBaseProcessor
import com.example.demoproject.View.JsonDataHolder
import com.example.demoproject.View.PoseViewModel
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors


abstract class MLVideoHelperActivity : AppCompatActivity(){
    protected var previewView: PreviewView? = null
    protected var graphicOverlay: GraphicOverlay? = null
    private var outputTextView: TextView? = null
    private var startButton: Button? = null
    private var stopButton: Button? = null
    private var viewButton: Button? = null
    private var scroll: ScrollView? = null
    private var jsonTx: TextView? = null
    private var isCollectingData = false
    private val pose: Pose? = null
    private val collectedData: MutableList<MutableList<PoseLandmark>> = mutableListOf()
    private var addFaceButton: ExtendedFloatingActionButton? = null
    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private lateinit var processor: VisionBaseProcessor<*>
    private var imageAnalysis: ImageAnalysis? = null
    private var jsonText: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mlvideo_helper)
        previewView = findViewById(R.id.camera_source_preview)
        graphicOverlay = findViewById(R.id.graphic_overlay)
        outputTextView = findViewById(R.id.output_text_view)
        scroll = findViewById(R.id.scrollView)
        jsonTx = findViewById(R.id.jsonTextView)
        startButton = findViewById(R.id.startBtn)
        stopButton = findViewById(R.id.stopBtn)
        viewButton = findViewById(R.id.viewBtn)
        addFaceButton = findViewById(R.id.button_add_face)

        startButton?.setOnClickListener {
            onStartClick()
        }

        stopButton?.setOnClickListener {
            onStopClick()
        }

        viewButton?.setOnClickListener {
            onViewCLick()
        }

        processor = setProcessor()
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        }

        val viewModel: PoseViewModel = ViewModelProvider(this)[PoseViewModel::class.java]
        viewModel.valueLiveData.observe(this) { newValue ->
            jsonText += newValue
            Log.d(TAG, "In Activity: $jsonText")
        }
    }

    private fun saveJsonToFile(jsonString: String, fileName: String) {
        try {
            val file = File(getExternalFilesDir(null), fileName)
            val fos = FileOutputStream(file)
            val osw = OutputStreamWriter(fos)
            osw.write(jsonString)
            osw.flush()
            osw.close()

            // Notify the user that the file has been saved
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    protected override fun onDestroy() {
        super.onDestroy()
        processor.stop()
    }

   private fun onStartClick() {
        isCollectingData = true
        startButton?.visibility = View.INVISIBLE
        stopButton?.visibility = View.VISIBLE
        graphicOverlay?.visibility = View.VISIBLE
        previewView?.visibility = View.VISIBLE
        if(cameraProviderFuture == null) {
            initSource()
        }
        collectedData.clear()
    }

    private fun onStopClick() {
        isCollectingData = false
        stopButton?.visibility = View.INVISIBLE
        previewView?.visibility = View.INVISIBLE
        graphicOverlay?.visibility = View.INVISIBLE
        viewButton?.visibility = View.VISIBLE
        Log.d("$TAG", jsonText)
    }

    private fun onViewCLick() {
        val intent = Intent(this, ViewJSON::class.java)
        JsonDataHolder.getInstance().setJsonData(jsonText)
        startActivity(intent)
        finish()

//        scroll?.visibility = View.VISIBLE
//        jsonTx?.text = jsonText
    }

    private val TAG = "PoseDetectorProcessor"

    private fun exportDataToJSON() {
        val jsonArray = JSONArray()
        for (poseLandmarks in collectedData) {
            val landmarksArray = JSONArray()
            for (landmark in poseLandmarks) {
                val landmarkObject = JSONObject()
                try {
                    landmarkObject.put("x", landmark.position.x.toDouble())
                    landmarkObject.put("y", landmark.position.y.toDouble())
                    landmarkObject.put("type", landmark.landmarkType)


                    landmarksArray.put(landmarkObject)
                } catch (e: JSONException) {
                    Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
            jsonArray.put(landmarksArray)
        }
        val jsonString = jsonArray.toString()

        // Save jsonString to a JSON file (e.g., using FileWriter or other methods)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //  initSource()
        }
    }

    protected fun setOutputText(text: String?) {
        outputTextView?.text = text
    }

    private fun initSource() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        cameraProviderFuture?.addListener(Runnable {
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture!!.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            } catch (e: InterruptedException) {
            }
        }, ContextCompat.getMainExecutor(applicationContext))
    }

    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val lensFacing = lensFacing
        val preview: Preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
        preview.setSurfaceProvider(previewView?.surfaceProvider)
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .build()
        setFaceDetector(lensFacing)
        cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)
    }

    /**
     * The face detector provides face bounds whose coordinates, width and height depend on the
     * preview's width and height, which is guaranteed to be available after the preview starts
     * streaming.
     */
    private fun setFaceDetector(lensFacing: Int) {
        previewView?.previewStreamState?.observe(this, object : Observer<PreviewView.StreamState> {
            override fun onChanged(streamState: PreviewView.StreamState) {
                if (streamState !== PreviewView.StreamState.STREAMING) {
                    return
                }
                val preview: View = previewView!!.getChildAt(0)
                var width = preview.width * preview.scaleX
                var height = preview.height * preview.scaleY
                val rotation = preview.display.rotation.toFloat()
                if (rotation == Surface.ROTATION_90.toFloat() || rotation == Surface.ROTATION_270.toFloat()) {
                    val temp = width
                    width = height
                    height = temp
                }
                imageAnalysis?.setAnalyzer(
                    executor,
                    createFaceDetector(width.toInt(), height.toInt(), lensFacing)
                )
                previewView!!.previewStreamState.removeObserver(this)
            }
        })
    }

    @OptIn(markerClass = [ExperimentalGetImage::class])
    private fun createFaceDetector(
        width: Int,
        height: Int,
        lensFacing: Int
    ): ImageAnalysis.Analyzer {

        graphicOverlay?.setPreviewProperties(width, height, lensFacing)
        return ImageAnalysis.Analyzer { imageProxy ->
            if (imageProxy.image == null) {
                imageProxy.close()
                return@Analyzer
            }
            val rotationDegrees: Int = imageProxy.imageInfo.rotationDegrees
            // converting from YUV format
            processor.detectInImage(imageProxy, toBitmap(imageProxy.image!!), rotationDegrees)

//            Log.d(, processor.pose.allPoseLandmarks[0].position.x.toString())


            // after done, release the ImageProxy object
            imageProxy.close()
        }
    }

    private fun toBitmap(image: Image): Bitmap {
        val planes: Array<Image.Plane> = image.planes
        val yBuffer: ByteBuffer = planes[0].buffer
        val uBuffer: ByteBuffer = planes[1].buffer
        val vBuffer: ByteBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val nv21 = ByteArray(ySize + uSize + vSize)
        //U and V are swapped
        yBuffer[nv21, 0, ySize]
        vBuffer[nv21, ySize, vSize]
        uBuffer[nv21, ySize + vSize, uSize]
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    protected val lensFacing: Int
        get() = CameraSelector.LENS_FACING_FRONT

    protected abstract fun setProcessor(): VisionBaseProcessor<*>
    fun makeAddFaceVisible() {
        addFaceButton!!.visibility = View.VISIBLE
    }

    fun onAddFaceClicked(view: View?) {}

    companion object {
        private const val REQUEST_CAMERA = 1001
    }
}


