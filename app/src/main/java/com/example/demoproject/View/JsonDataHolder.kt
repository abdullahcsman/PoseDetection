package com.example.demoproject.View

class JsonDataHolder private constructor() {
    private var jsonData: String? = null

    companion object {
        private var instance: JsonDataHolder? = null

        @Synchronized
        fun getInstance(): JsonDataHolder {
            if (instance == null) {
                instance = JsonDataHolder()
            }
            return instance as JsonDataHolder
        }
    }

    fun getJsonData(): String? {
        return jsonData
    }

    fun setJsonData(jsonData: String) {
        this.jsonData = jsonData
    }
}
