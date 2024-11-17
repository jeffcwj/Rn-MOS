package com.gtastart.common.util.compose.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import com.gtastart.common.theme.GtaStartTheme
import com.valvesoftware.source.R

@Composable
fun DefaultLoadingPlaceHolder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(GtaStartTheme.spacing.normal),
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DefaultLoadFailedPlaceHolder(
    modifier: Modifier = Modifier,
    text: String = stringResource(R.string.load_failed),
    buttonText: String = stringResource(R.string.retry),
    retryOnClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .padding(GtaStartTheme.spacing.medium)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = text)
        if (retryOnClick != null) {
            Button(
                modifier = modifier.padding(top = GtaStartTheme.spacing.normal),
                onClick = {
                    retryOnClick.invoke()
                }
            ) {
                Text(text = buttonText)
            }
        }
    }
}

@Composable
fun ImagePlaceHolder(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(R.drawable.ic_launcher)
) {
    MImage(
        modifier = modifier,
        painter = painter
    )
}


