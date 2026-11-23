package com.meal.presentation.recipes

/**
 * Numbering or a bullet at the start of a pasted line: "1.", "2)", "Step 3:", "-", "•" or "*".
 * A number needs a "." or ")" and a following space, so "2 cups of water" and "1.5 kg pork"
 * keep their numbers.
 */
private val leadingMarker = Regex("""^(?:step\s*\d+\s*[:.)\-]?|\d+\s*[.)]|[-•*])\s+""", RegexOption.IGNORE_CASE)

/**
 * The recipe steps in [text], one per line: blank lines are dropped, and each line loses its
 * surrounding spaces and any leading numbering or bullet, since the app numbers steps itself.
 */
fun splitInstructionLines(text: String): List<String> =
    text.lines()
        .map { it.trim().replace(leadingMarker, "").trim() }
        .filter { it.isNotEmpty() }

/**
 * What an edit of one step turns into, given its text [before] and [after] the edit.
 *
 * Typing a single line break splits the step there, keeping both halves as typed, so Enter at
 * the end of a step opens an empty step below it. Any other edit that brings in line breaks,
 * such as a paste, becomes one step per line as [splitInstructionLines] reads them. An edit
 * without line breaks stays a single step.
 */
fun stepsFromEdit(before: String, after: String): List<String> {
    if ('\n' !in after) return listOf(after)
    if (isTypedLineBreak(before, after)) return after.split('\n')
    return splitInstructionLines(after).ifEmpty { listOf("") }
}

/** Whether the edit from [before] to [after] is a single typed line break, as when Enter is pressed. */
fun isTypedLineBreak(before: String, after: String): Boolean =
    after.length == before.length + 1 && after.count { it == '\n' } == 1 && before.count { it == '\n' } == 0
