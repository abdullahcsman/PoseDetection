package com.example.demoproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.demoproject.HelperClasses.PoseDetectorProcessor
import com.example.demoproject.HelperClasses.VisionBaseProcessor
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions


class MainActivity : MLVideoHelperActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Pointss", "App come here1")
        Toast.makeText(this, "App come here1", Toast.LENGTH_LONG).show()

    }

    override fun setProcessor(): VisionBaseProcessor<*> {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
        return PoseDetectorProcessor(
            options,
            true,
            false,
            false,
            false,
            true,
            this,
            graphicOverlay,
            previewView
        )
    }
}