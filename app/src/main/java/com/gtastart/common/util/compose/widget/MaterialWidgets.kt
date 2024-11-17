package com.gtastart.common.util.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

enum class ButtonType {
    Filled,
    Outlined
}

@Composable
fun MButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = LocalTextStyle.current,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: (() -> Unit) = {},
    btnType: ButtonType = ButtonType.Filled
) {
    if (btnType == ButtonType.Filled) {
        Button(
            modifier = modifier,
            onClick = {
                onClick.invoke()
            },
            contentPadding = contentPadding
        ) {
            Text(
                style = textStyle,
                text = text,
            )
        }
    } else if (btnType == ButtonType.Outlined) {
        OutlinedButton (
            modifier = modifier,
            onClick = {
                onClick.invoke()
            },
            contentPadding = contentPadding
        ) {
            Text(
                style = textStyle,
                text = text,
            )
        }
    }

}

@Composable
fun MImage(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 1.0f
) {
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
        contentScale = contentScale,
        alpha = alpha
    )
}

@Composable
fun MIconButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription
        )
    }
}