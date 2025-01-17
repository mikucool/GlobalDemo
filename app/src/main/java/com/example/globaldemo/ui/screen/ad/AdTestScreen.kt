package com.example.globaldemo.ui.screen.ad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AdTestScreen(
    modifier: Modifier = Modifier,
    onLoadAdClicked: () -> Unit = {},
    onShowAdClicked: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onLoadAdClicked.invoke() },
            shape = CircleShape
        ) {
            Text(text = "Load Ad")
        }

        Button(onClick = {
            onShowAdClicked.invoke()
        }) {
            Text(text = "Show Ad")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdTestScreenPreview() {
    AdTestScreen()
}