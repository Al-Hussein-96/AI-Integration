package com.alhussain.aiintegration.viewModels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.alhussain.aiintegration.helpers.OcrEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OcrViewModel : ViewModel() {

    private val _textResult = MutableStateFlow("")
    val textResult: StateFlow<String> = _textResult

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun runOcr(bitmap: Bitmap) {
        _loading.value = true

        OcrEngine.recognize(
            bitmap = bitmap,
            onResult = {
                _textResult.value = it
                _loading.value = false
            },
            onError = {
                _textResult.value = "Error: ${it.message}"
                _loading.value = false
            }
        )
    }
}