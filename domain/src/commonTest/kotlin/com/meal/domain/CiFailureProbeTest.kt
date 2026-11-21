package com.meal.domain

import kotlin.test.Test
import kotlin.test.fail

class CiFailureProbeTest {

    @Test
    fun alwaysFails() {
        fail("Deliberate failure to confirm the PR check blocks failing tests.")
    }
}
