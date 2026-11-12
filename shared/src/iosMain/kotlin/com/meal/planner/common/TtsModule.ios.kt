package com.meal.planner.common

import com.meal.domain.service.TtsSpeaker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.module.Module
import org.koin.dsl.module

// Not implemented: this machine has no Xcode toolchain to build/verify an
// AVSpeechSynthesizer-based implementation. All operations are no-ops
// until an iOS actual is implemented and tested.
private class NoopTtsSpeaker : TtsSpeaker {
    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    override fun speak(text: String, onFinished: ((completedNormally: Boolean) -> Unit)?) {}

    override fun stop() {}
}

actual val ttsModule: Module = module {
    single<TtsSpeaker> { NoopTtsSpeaker() }
}
