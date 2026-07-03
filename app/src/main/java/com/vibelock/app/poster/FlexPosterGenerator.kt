package com.vibelock.app.poster

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.vibelock.app.engine.AuraTier
import java.io.File
import java.io.FileOutputStream

@Composable
fun FlexPosterLayout(
    tier: AuraTier,
    streak: Int
) {
    // 1080x1920 logical layout
    Box(
        modifier = Modifier
            .size(1080.dp, 1920.dp)
            .background(Color(0xFF0F0F0F)), // Deep dark premium background
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "✨",
                fontSize = 120.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = tier.title,
                fontSize = 80.sp,
                fontWeight = FontWeight.Black,
                color = Color(tier.colorHex)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${streak}x STREAK MULTIPLIER",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC0C0C0) // Metallic Typography
            )
            Spacer(modifier = Modifier.height(120.dp))
            Text(
                text = "VIBELOCK.APP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

object FlexPosterGenerator {
    fun exportAndShare(activity: Activity, viewToCapture: View, onComplete: () -> Unit) {
        val bitmap = Bitmap.createBitmap(viewToCapture.width, viewToCapture.height, Bitmap.Config.ARGB_8888)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PixelCopy.request(activity.window, viewToCapture.apply { 
                // get coordinates of view
            }.run { 
                android.graphics.Rect(left, top, right, bottom)
            }, bitmap, { result ->
                if (result == PixelCopy.SUCCESS) {
                    shareBitmap(activity, bitmap)
                    onComplete()
                }
            }, Handler(Looper.getMainLooper()))
        } else {
            // Fallback for older devices if we targeted < 26
        }
    }

    private fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "vibelock_flex.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "Flex Your Rank ✨"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
