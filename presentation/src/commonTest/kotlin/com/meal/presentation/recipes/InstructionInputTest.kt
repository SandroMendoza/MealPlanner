package com.meal.presentation.recipes

import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionInputTest {

    @Test
    fun pastedLinesLoseNumberingBulletsAndBlankLines() {
        val pasted = """
            Step 1: Wash the rice
            Step 2 - Boil water

            3. Add rice
            4) Simmer for 20 minutes
            - Fluff with a fork
            • Serve
            * Enjoy
        """.trimIndent()

        assertEquals(
            listOf("Wash the rice", "Boil water", "Add rice", "Simmer for 20 minutes", "Fluff with a fork", "Serve", "Enjoy"),
            splitInstructionLines(pasted)
        )
    }

    @Test
    fun aLineThatOnlyStartsWithANumberKeepsIt() {
        assertEquals(
            listOf("2 cups of water", "1.5 kg pork belly", "350F oven"),
            splitInstructionLines("2 cups of water\n1.5 kg pork belly\r\n350F oven")
        )
    }

    @Test
    fun anEditWithoutLineBreaksStaysOneStep() {
        assertEquals(listOf("Boil water"), stepsFromEdit("Boil wate", "Boil water"))
    }

    @Test
    fun typingEnterSplitsTheStepAtTheCursorAsTyped() {
        assertEquals(listOf("Boil water", ""), stepsFromEdit("Boil water", "Boil water\n"))
        assertEquals(listOf("Boil", " water"), stepsFromEdit("Boil water", "Boil\n water"))
        assertEquals(listOf("1. Keep my number", ""), stepsFromEdit("1. Keep my number", "1. Keep my number\n"))
    }

    @Test
    fun pastingSeveralLinesGivesOneCleanStepPerLine() {
        assertEquals(
            listOf("Wash rice", "Boil water"),
            stepsFromEdit("", "1. Wash rice\n2. Boil water\n")
        )
    }

    @Test
    fun pastingOnlyBlankLinesLeavesOneEmptyStep() {
        assertEquals(listOf(""), stepsFromEdit("", "\n\n"))
    }
}
