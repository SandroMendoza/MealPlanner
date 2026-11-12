package com.meal.domain.service

import kotlinx.coroutines.flow.StateFlow

interface TtsSpeaker {
    val isSpeaking: StateFlow<Boolean>

    /**
     * Speaks [text], interrupting any utterance currently in progress. [onFinished], if given, is
     * invoked exactly once when this utterance stops being the active one: with `true` if it
     * finished on its own, or `false` if it was cut short (interrupted by another [speak] call,
     * or by [stop]) before it could.
     */
    fun speak(text: String, onFinished: ((completedNormally: Boolean) -> Unit)? = null)

    fun stop()
}
