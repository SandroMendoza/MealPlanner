package com.meal.planner.ads

import android.app.Activity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Gathers ad consent through Google's User Messaging Platform and starts the Mobile Ads SDK
 * once ads may be requested. Users in regions that need consent (EEA, UK, Switzerland) see
 * the consent form; everyone else gets ads straight away.
 */
object AdsConsent {
    private val _canRequestAds = MutableStateFlow(false)
    val canRequestAds: StateFlow<Boolean> = _canRequestAds.asStateFlow()

    private val _privacyOptionsRequired = MutableStateFlow(false)
    val privacyOptionsRequired: StateFlow<Boolean> = _privacyOptionsRequired.asStateFlow()

    private val mobileAdsStarted = AtomicBoolean(false)

    /** Call on every launch, so consent changes and expiries are picked up. */
    fun gatherConsent(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)
        consentInformation.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                    onConsentResolved(activity, consentInformation)
                }
            },
            { onConsentResolved(activity, consentInformation) }
        )
        // Consent stored from an earlier launch lets ads start before the update returns.
        onConsentResolved(activity, consentInformation)
    }

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            onConsentResolved(activity, UserMessagingPlatform.getConsentInformation(activity))
        }
    }

    private fun onConsentResolved(activity: Activity, consentInformation: ConsentInformation) {
        _privacyOptionsRequired.value = consentInformation.privacyOptionsRequirementStatus ==
            ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        if (!consentInformation.canRequestAds() || !mobileAdsStarted.compareAndSet(false, true)) return
        val appContext = activity.applicationContext
        // Google recommends initializing the Mobile Ads SDK off the main thread.
        CoroutineScope(Dispatchers.IO).launch {
            MobileAds.initialize(appContext)
            _canRequestAds.value = true
        }
    }
}
