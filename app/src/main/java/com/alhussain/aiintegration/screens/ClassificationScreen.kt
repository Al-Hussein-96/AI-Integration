package com.alhussain.aiintegration.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alhussain.aiintegration.viewModels.ClassificationViewModel


@Composable
fun AiScreen(
    modifier: Modifier = Modifier,
    sampleBitmap: Bitmap,
    viewModel: ClassificationViewModel = viewModel(),
) {
    // Observe the StateFlow from the ViewModel safely in Compose
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Show the image we are going to classify
        Image(
            bitmap = sampleBitmap.asImageBitmap(),
            contentDescription = "Sample Image",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Show the current AI status (Loading, Ready, Analyzing, or the Result)
        Text(text = uiState)



        Spacer(modifier = Modifier.height(20.dp))

        // Trigger the AI Inference
        Button(
            onClick = { viewModel.analyzeBitmap(sampleBitmap) },
            // Disable button if model is loading or currently analyzing
            enabled = uiState.contains("Ready") || uiState.contains("Detected") || uiState.contains("Unknown")
        ) {
            Text("Classify Image")
        }
    }
}
