package com.example.globaldemo.ui.screen.test

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.globaldemo.ad.controller.BiddingAdController

@Composable
fun RewardAdPlatformTestScreen(
    modifier: Modifier = Modifier,
    biddingAdControllers: List<BiddingAdController> = emptyList(),
    viewModel: RewardAdTestViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState.collectAsState()
    if (state.value.rewardTestRewardAdState == TestRewardAdState.LOADING) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
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
                biddingAdControllers.forEach { adController ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.toggleable(
                            value = state.value.adPlatform == adController.adConfiguration.adPlatform,
                            enabled = true,
                            onValueChange = { viewModel.updateAdPlatform(adController.adConfiguration.adPlatform) })
                    ) {
                        RadioButton(
                            selected = state.value.adPlatform == adController.adConfiguration.adPlatform,
                            onClick = { viewModel.updateAdPlatform(adController.adConfiguration.adPlatform) }
                        )
                        Text(text = adController.adConfiguration.adPlatform.name)
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        val controller =
                            biddingAdControllers.find { it.adConfiguration.adPlatform == state.value.adPlatform }
                        controller?.let { viewModel.loadRewardAd(context, it) }
                    },
                    shape = CircleShape
                ) {
                    Text(text = "Load Reward Ad")
                }

                Button(onClick = {
                    val controller = biddingAdControllers.find { it.adConfiguration.adPlatform == state.value.adPlatform }
                    if (controller != null) {
                        viewModel.displayRewardAd(context as Activity, controller)
                    }
                }) {
                    Text(text = "Show Reward Ad")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdTestScreenPreview() {
    RewardAdPlatformTestScreen()
}
