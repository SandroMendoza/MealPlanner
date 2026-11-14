package com.meal.presentation.fakes

import com.meal.domain.model.MealReminder
import com.meal.domain.model.enums.MealType
import com.meal.domain.service.ReminderScheduler
import com.meal.domain.service.TtsSpeaker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeTtsSpeaker : TtsSpeaker {
    private val speaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = speaking

    val spoken = mutableListOf<String>()
    var stopCount = 0
        private set
    private var pendingCallback: ((Boolean) -> Unit)? = null

    override fun speak(text: String, onFinished: ((completedNormally: Boolean) -> Unit)?) {
        spoken += text
        speaking.value = true
        pendingCallback = onFinished
    }

    override fun stop() {
        stopCount++
        speaking.value = false
    }

    /** Ends the current utterance the way the platform engine would, invoking its callback once. */
    fun finishCurrent(completedNormally: Boolean = true) {
        val callback = pendingCallback
        pendingCallback = null
        speaking.value = false
        callback?.invoke(completedNormally)
    }
}

class FakeReminderScheduler : ReminderScheduler {
    val scheduled = mutableListOf<MealReminder>()
    val cancelled = mutableListOf<MealType>()

    override fun schedule(reminder: MealReminder) {
        scheduled += reminder
    }

    override fun cancel(mealType: MealType) {
        cancelled += mealType
    }

    override fun rescheduleAll(reminders: List<MealReminder>) {
        scheduled += reminders
    }
}
