package com.gtastart.common.util.compose.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.valvesoftware.source.R


@Composable
fun WpImage(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    url: String?,
    contentScale: ContentScale = ContentScale.Crop
) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .placeholder(R.drawable.ic_launcher)
            .error(R.drawable.ic_launcher)
//            .addHeader("User-Agent", WPConstants.WP_USER_AGENT)
            .build(),
        imageLoader = ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
                add(GifDecoder.Factory())
            }.build(),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}