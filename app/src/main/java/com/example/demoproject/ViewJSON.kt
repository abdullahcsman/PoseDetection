package com.example.demoproject

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.demoproject.R
import com.example.demoproject.View.JsonDataHolder

class ViewJSON : AppCompatActivity() {
    private var jsonTextView: TextView? = null
    private var viewRawButton: Button? = null
    private var isRawView = false
    private var sampleJson: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_json)
        jsonTextView = findViewById(R.id.jsonTextView)
        viewRawButton = findViewById(R.id.viewRawButton)
        sampleJson = JsonDataHolder.getInstance().getJsonData()
        if (sampleJson != null) {
            // Process the JSON string as needed
            // For example, you might want to parse it using a JSON library like Gson
            updateJsonText(sampleJson)
        }

        // Toggle between Raw and Formatted JSON view
        viewRawButton?.setOnClickListener(View.OnClickListener {
            if (isRawView) {
                updateJsonText(formatJson(sampleJson))
                viewRawButton?.setText("View Raw JSON")
            } else {
                updateJsonText(sampleJson)
                viewRawButton?.setText("View Formatted JSON")
            }
            isRawView = !isRawView
        })
    }

    private fun updateJsonText(json: String?) {
        jsonTextView!!.text = json
    }

    private fun formatJson(json: String?): String? {
        // Implement your JSON formatting logic here
        // For example, you can use libraries like Gson or JSONObject
        // to format the JSON in a more readable way.
        return json // Returning the raw JSON for demonstration purposes
    }
}