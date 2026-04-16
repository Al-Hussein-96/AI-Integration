package com.alhussain.aiintegration

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiViewModel(application: Application) : AndroidViewModel(application) {

    private val classifierHelper = ImageClassifierHelper(application)

    // UI State Management
    private val _uiState = MutableStateFlow("Initializing Model...")
    val uiState = _uiState.asStateFlow()

    init {
        // Load model in background when ViewModel is created
        viewModelScope.launch(Dispatchers.IO) {
            try {
                classifierHelper.setup()
                _uiState.value = "Model Ready! Tap to classify."
            } catch (e: Exception) {
                _uiState.value = "Failed to load model: ${e.message}"
            }
        }
    }

    fun analyzeBitmap(bitmap: Bitmap) {
        _uiState.value = "Analyzing..."

        // Switch to CPU-optimized thread for heavy math (Inference)
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = classifierHelper.classifyImage(bitmap)

                // Switch back to Main Thread to update UI State
                withContext(Dispatchers.Main) {
                    _uiState.value = result
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.value = "Error during inference"
                }
            }
        }
    }

    // CRITICAL: Prevent OutOfMemory (OOM) crashes
    override fun onCleared() {
        super.onCleared()
        classifierHelper.close()
    }
}