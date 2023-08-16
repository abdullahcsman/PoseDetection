package com.example.demoproject.View;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PoseViewModel extends ViewModel {
    // Define a LiveData to hold the value
    private MutableLiveData<String> valueLiveData = new MutableLiveData<>();

    // Method to update the value
    public void updateValue(String newValue) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            valueLiveData.setValue(newValue);
        } else {
            // If not on the main thread, use postValue to switch to the main thread
            valueLiveData.postValue(newValue);
        }
    }

    // Method to get the LiveData
    public LiveData<String> getValueLiveData() {
        return valueLiveData;
    }
}
