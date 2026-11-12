package com.meal.planner.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.meal.domain.service.TtsSpeaker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AndroidTtsSpeaker(context: Context) : TtsSpeaker {
    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())
    private var ready = false
    private val pendingOnFinished = mutableMapOf<String, (Boolean) -> Unit>()

    // UtteranceProgressListener callbacks run on the TTS engine's own binder thread, not the
    // main thread. Calling back into the engine (e.g. speak() from a finished callback, as cook
    // mode's auto-advance does) from that same thread is a reentrant call the engine isn't
    // designed for, and causes audible glitches/restarts on real devices. Posting to the main
    // thread first decouples the callback from the engine's call stack.
    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        mainHandler.post { ready = status == TextToSpeech.SUCCESS }
    }.apply {
        setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                mainHandler.post { _isSpeaking.value = true }
            }

            override fun onDone(utteranceId: String) {
                mainHandler.post {
                    _isSpeaking.value = false
                    pendingOnFinished.remove(utteranceId)?.invoke(true)
                }
            }

            // A newer speak() call (QUEUE_FLUSH) or an explicit stop() cuts this utterance short
            // before onDone fires for it. Without handling this, whichever button started it is
            // left showing its "speaking" state forever, since nothing ever tells it to stop.
            override fun onStop(utteranceId: String, interrupted: Boolean) {
                mainHandler.post {
                    _isSpeaking.value = false
                    pendingOnFinished.remove(utteranceId)?.invoke(false)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) {
                mainHandler.post {
                    _isSpeaking.value = false
                    pendingOnFinished.remove(utteranceId)?.invoke(false)
                }
            }
        })
    }

    override fun speak(text: String, onFinished: ((completedNormally: Boolean) -> Unit)?) {
        if (!ready) return
        val utteranceId = UUID.randomUUID().toString()
        if (onFinished != null) pendingOnFinished[utteranceId] = onFinished
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun stop() {
        tts.stop()
        pendingOnFinished.clear()
        _isSpeaking.value = false
    }
}
