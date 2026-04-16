package com.alhussain.aiintegration.helpers

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifierHelper(private val context: Context) {

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    // The model expects a specific image size (e.g., 224x224)
    private val targetWidth = 224
    private val targetHeight = 224

    fun setup() {
        try {
            // 1. Load the model from the assets folder into a ByteBuffer
            val modelBuffer = loadModelFile("mobilenet_v1_1.0_224_quant.tflite")

            // 2. Initialize the TFLite Interpreter
            val options = Interpreter.Options().apply {
                setNumThreads(4) // Use 4 CPU threads for faster processing
            }
            interpreter = Interpreter(modelBuffer, options)

            // 3. Load the labels (e.g., "Cat", "Dog", "Car")
            labels = loadLabels("labels_mobilenet_quant_v1_224.txt")
        } catch (e: Exception) {
            throw RuntimeException("Failed to load TFLite model: ${e.message}", e)
        }
    }

    fun classifyImage(bitmap: Bitmap): String {
        val tflite = interpreter ?: return "Model not initialized"

        try {
            // 4. Resize the bitmap to 224x224
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

            // 5. Convert Bitmap to ByteBuffer
            val inputBuffer = bitmapToByteBuffer(resizedBitmap)

            // 6. Prepare the output buffer
            // MobileNet returns an array of probabilities (one for each label)
            val probabilityBuffer = Array(1) { ByteArray(labels.size) }

            // 7. RUN INFERENCE (This is the CPU-heavy math part!)
            tflite.run(inputBuffer, probabilityBuffer)

            // 8. Parse the results to find the highest probability
            return getTopResult(probabilityBuffer[0])
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error during inference: ${e.message}"
        }
    }

    private fun bitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(targetWidth * targetHeight * 3) // no ×4
        byteBuffer.order(ByteOrder.nativeOrder())

        val intPixels = IntArray(targetWidth * targetHeight)
        bitmap.getPixels(intPixels, 0, targetWidth, 0, 0, targetWidth, targetHeight)

        for (argb in intPixels) {
            byteBuffer.put(((argb shr 16) and 0xFF).toByte()) // R
            byteBuffer.put(((argb shr 8) and 0xFF).toByte())  // G
            byteBuffer.put((argb and 0xFF).toByte())           // B
        }
        byteBuffer.rewind()
        return byteBuffer
    }

    private fun loadModelFile(fileName: String): ByteBuffer {
        val inputStream = context.assets.open(fileName)
        val buffer = ByteBuffer.allocateDirect(inputStream.available())
        buffer.order(ByteOrder.nativeOrder())
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        buffer.put(bytes)
        buffer.rewind()
        return buffer
    }

    private fun loadLabels(fileName: String): List<String> {
        val labels = mutableListOf<String>()
        val inputStream = context.assets.open(fileName)
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                labels.add(line!!)
            }
        }
        return labels
    }

    private fun getTopResult(probabilities: ByteArray): String {
        data class Result(val label: String, val confidence: Float)

        val results = probabilities.mapIndexed { index, byte ->
            val confidence = (byte.toInt() and 0xFF) / 255.0f
            Result(labels.getOrElse(index) { "Unknown" }, confidence)
        }

        val top3 = results
            .sortedByDescending { it.confidence }
            .take(3)

        val result = top3.joinToString("\n") {
            "${it.label}: ${(it.confidence * 100).toInt()}%"
        }

        println("result: $result")

        return result
    }

    // CRITICAL: Clean up Native Memory!
    fun close() {
        interpreter?.close()
        interpreter = null
    }
}