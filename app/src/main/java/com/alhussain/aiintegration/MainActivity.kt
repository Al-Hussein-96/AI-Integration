package com.alhussain.aiintegration

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.alhussain.aiintegration.screens.AiScreen
import com.alhussain.aiintegration.ui.theme.AIIntegrationTheme

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Classification : Screen("classification")
    object Recognition : Screen("recognition")
}

// ----------------------
// DATA MODEL
// ----------------------
data class AiFeature(
    val title: String,
    val description: String,
    val icon: String = "🤖",
    val onClick: () -> Unit
)

// ----------------------
// MAIN ACTIVITY
// ----------------------
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AIIntegrationTheme {
                val navController = androidx.navigation.compose.rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        AiHomeScreen(
                            onNavigateToAi = { routeName ->
                                navController.navigate(routeName)
                            }
                        )
                    }
                    composable(Screen.Classification.route) {
                        val sampleBitmap = BitmapFactory.decodeResource(
                            resources,
                            R.drawable.dog
                        )
                        AiScreen(
                            sampleBitmap = sampleBitmap // pass your bitmap here
                        )
                    }
                    composable(Screen.Recognition.route) {
                        val sampleBitmap = BitmapFactory.decodeResource(
                            resources,
                            R.drawable.dog
                        )
                        AiScreen(
                            sampleBitmap = sampleBitmap // pass your bitmap here
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun AiHomeScreen(onNavigateToAi: (String) -> Unit, modifier: Modifier = Modifier) {

    val features = listOf(
        AiFeature(
            title = "Image Classification",
            description = "Detect objects using MobileNet",
            icon = "🖼️",
            onClick = {
                onNavigateToAi.invoke("classification")

            }
        ),
        AiFeature(
            title = "Text Recognition",
            description = "Extract text from images (OCR)",
            icon = "📝",
            onClick = {
                onNavigateToAi.invoke("recognition")
            }
        ),
        AiFeature(
            title = "Object Detection",
            description = "Detect multiple objects in image",
            icon = "🎯",
            onClick = { }
        ),
        AiFeature(
            title = "Image Enhancement",
            description = "Improve image quality using AI",
            icon = "✨",
            onClick = { }
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Choose AI Tool",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(features) { feature ->
                AiFeatureCard(feature)
            }
        }
    }
}

@Composable
fun AiFeatureCard(feature: AiFeature) {

    Card(
        onClick = feature.onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = feature.icon,
                style = MaterialTheme.typography.headlineMedium
            )

            Column {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

//// 2. Create a sample Bitmap to test the AI with.
//// We use the default Android launcher icon because every project has it.

//
//// 3. Render the UI, passing the innerPadding from Scaffold
//AiScreen(
//sampleBitmap = sampleBitmap,
//modifier = Modifier.padding(innerPadding)
//)
