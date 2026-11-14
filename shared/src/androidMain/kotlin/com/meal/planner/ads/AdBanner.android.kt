package com.meal.planner.ads

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
actual fun AdBanner(placement: AdPlacement, modifier: Modifier) {
    val canRequestAds by AdsConsent.canRequestAds.collectAsStateWithLifecycle()
    if (!canRequestAds) return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val widthDp = maxWidth.value.toInt()
        var loaded by remember(placement, widthDp) { mutableStateOf(false) }
        val adView = remember(placement, widthDp) {
            AdView(context).apply {
                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp))
                adUnitId = adUnitIdFor(context, placement)
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        loaded = true
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        loaded = false
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }

        DisposableEffect(adView, lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> adView.resume()
                    Lifecycle.Event.ON_PAUSE -> adView.pause()
                    else -> Unit
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                adView.destroy()
            }
        }

        AndroidView(
            factory = { adView },
            modifier = if (loaded) Modifier.fillMaxWidth() else Modifier.height(0.dp)
        )
    }
}

@Composable
actual fun rememberAdPrivacyOptions(): (() -> Unit)? {
    val required by AdsConsent.privacyOptionsRequired.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    if (!required || activity == null) return null
    return { AdsConsent.showPrivacyOptions(activity) }
}

// Ad unit IDs are set per build type in androidApp/build.gradle.kts and reach this module
// as application meta-data, since this library module has no BuildConfig of its own.
private fun adUnitIdFor(context: Context, placement: AdPlacement): String {
    val key = when (placement) {
        AdPlacement.HOME -> "com.meal.planner.ads.BANNER_HOME"
        AdPlacement.MEAL_PLAN -> "com.meal.planner.ads.BANNER_MEAL_PLAN"
    }
    val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
    return appInfo.metaData?.getString(key).orEmpty()
}
