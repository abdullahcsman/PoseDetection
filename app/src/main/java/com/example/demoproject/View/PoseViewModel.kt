package com.example.demoproject.View

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import android.os.Looper
import androidx.lifecycle.LiveData

class PoseViewModel : ViewModel() {
    // Define a LiveData to hold the value
    val valueLiveData = MutableLiveData<String>()

    // Method to update the value
    fun updateValue(newValue: String) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            valueLiveData.setValue(newValue)
        } else {
            // If not on the main thread, use postValue to switch to the main thread
            valueLiveData.postValue(newValue)
        }
    }

    // Method to get the LiveData
    fun getValueLiveData(): LiveData<String> {
        return valueLiveData
    }
}