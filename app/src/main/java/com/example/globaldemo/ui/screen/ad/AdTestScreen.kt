package com.example.globaldemo.ui.screen.ad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.globaldemo.ad.AdController

@Composable
fun AdTestScreen(
    modifier: Modifier = Modifier,
    adControllers: List<AdController> = emptyList(),
    viewModel: AdTestViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsState()
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            adControllers.forEach { adController ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.toggleable(
                        value = state.value.adPlatform == adController.adPlatform,
                        enabled = true,
                        onValueChange = { viewModel.updateAdPlatform(adController.adPlatform) })
                ) {
                    RadioButton(
                        selected = state.value.adPlatform == adController.adPlatform,
                        onClick = { viewModel.updateAdPlatform(adController.adPlatform) }
                    )
                    Text(text = adController.adPlatform.name)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    val controller = adControllers.find { it.adPlatform == state.value.adPlatform }
                    controller?.loadRewardVideoAd()
                },
                shape = CircleShape
            ) {
                Text(text = "Load Ad")
            }

            Button(onClick = {
                val controller = adControllers.find { it.adPlatform == state.value.adPlatform }
                controller?.showRewardVideoAd()
            }) {
                Text(text = "Show Ad")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdTestScreenPreview() {
    AdTestScreen()
}