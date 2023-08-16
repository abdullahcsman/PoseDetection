package com.example.demoproject

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.example.demoproject.R
import com.example.demoproject.View.JsonDataHolder

class ViewJSON : AppCompatActivity() {
    private var jsonTextView: TextView? = null
    private var isRawView = false
    private var sampleJson: String? = ""
    private var displayedText: String? = ""
    private lateinit var scrollView: NestedScrollView
    private lateinit var contentLayout: LinearLayout

    private val chunkSize = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_json)
        scrollView = findViewById(R.id.scroll)
        contentLayout = findViewById(R.id.contentLayout)
        sampleJson = JsonDataHolder.getInstance().getJsonData()

        scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY + scrollView.height >= contentLayout.bottom && displayedText!!.length < sampleJson!!.length) {
                val endIndex = minOf(displayedText!!.length + chunkSize, sampleJson!!.length)
                val newContent = sampleJson!!.substring(displayedText!!.length, endIndex)
                addTextToLayout(newContent)
                displayedText += newContent
            }
        }

        // Load initial content
        val initialContent = sampleJson!!.substring(0, minOf(chunkSize, sampleJson!!.length))
        addTextToLayout(initialContent)
        displayedText += initialContent
    }


    private fun addTextToLayout(text: String) {
        // Create a TextView and add it to the contentLayout
        val textView = TextView(this)
        textView.text = text
        contentLayout.addView(textView)
    }

    private fun updateJsonText(json: String?) {
//        jsonTextView!!.text = json
    }

    private fun formatJson(json: String?): String? {
        // Implement your JSON formatting logic here
        // For example, you can use libraries like Gson or JSONObject
        // to format the JSON in a more readable way.
        return json // Returning the raw JSON for demonstration purposes
    }
}