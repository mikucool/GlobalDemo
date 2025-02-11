package com.example.globaldemo.ui.screen.test

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.globaldemo.domain.AdUseCase
import com.example.globaldemo.ad.constant.RewardAdState

@Composable
fun BiddingAdTestScreen(
    modifier: Modifier = Modifier,
    adUseCase: AdUseCase = AdUseCase(),
    viewModel: BiddingAdTestViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.uiState.collectAsState()
    if (state.value.rewardAdState == RewardAdState.LOADING) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { viewModel.loadAd(context, adUseCase) }) {
                    Text(text = "Load Ad")
                }
                Button(onClick = { viewModel.displayRewardedAd(context as Activity, adUseCase) }) {
                    Text(text = "Show Ad")
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun BiddingAdTestScreenPreview() {
    BiddingAdTestScreen()
}