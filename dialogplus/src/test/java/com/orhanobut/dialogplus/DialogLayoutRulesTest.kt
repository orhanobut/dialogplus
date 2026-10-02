package com.orhanobut.dialogplus

import android.view.Gravity
import org.junit.Assert.*
import org.junit.Test

class DialogLayoutRulesTest {
    @Test fun onlyUnsetMarginsReceiveGravityDefaults() {
        assertEquals(16, DialogLayoutRules.margin(Gravity.CENTER, -1, 16))
        assertEquals(0, DialogLayoutRules.margin(Gravity.BOTTOM, -1, 16))
        assertEquals(0, DialogLayoutRules.margin(Gravity.TOP or Gravity.CENTER_HORIZONTAL, -1, 16))
        for (gravity in listOf(Gravity.CENTER, Gravity.TOP, Gravity.BOTTOM)) {
            assertEquals(0, DialogLayoutRules.margin(gravity, 0, 16))
            assertEquals(12, DialogLayoutRules.margin(gravity, 12, 16))
            assertEquals(-8, DialogLayoutRules.margin(gravity, -8, 16))
        }
    }
    @Test fun defaultHeightUsesTwoFifthsOfAvailableSpace() {
        assertEquals(400, DialogLayoutRules.defaultHeight(1000))
        assertEquals(401, DialogLayoutRules.defaultHeight(1004))
        assertEquals(0, DialogLayoutRules.defaultHeight(0))
    }
    @Test fun draggingIsClampedToTheInitialHeightAndScreen() {
        assertEquals(400, DialogLayoutRules.dragHeight(420, -100f, Gravity.BOTTOM, 400, 1000))
        assertEquals(1000, DialogLayoutRules.dragHeight(950, 100f, Gravity.BOTTOM, 400, 1000))
        assertEquals(460, DialogLayoutRules.dragHeight(450, 10.9f, Gravity.BOTTOM, 400, 1000))
    }
    @Test fun topDialogsInvertDragDirection() {
        assertEquals(490, DialogLayoutRules.dragHeight(500, 10f, Gravity.TOP, 400, 1000))
        assertEquals(510, DialogLayoutRules.dragHeight(500, -10f, Gravity.TOP, 400, 1000))
    }
    @Test fun upwardReleaseExpandsOnlyBeyondTheFiftyPixelThreshold() {
        assertEquals(400, DialogLayoutRules.snapHeight(449, true, 400, 1000))
        assertEquals(400, DialogLayoutRules.snapHeight(450, true, 400, 1000))
        assertEquals(1000, DialogLayoutRules.snapHeight(451, true, 400, 1000))
    }
    @Test fun downwardReleaseKeepsExpansionOnlyAboveTheScreenThreshold() {
        assertEquals(400, DialogLayoutRules.snapHeight(800, false, 400, 1000))
        assertEquals(1000, DialogLayoutRules.snapHeight(801, false, 400, 1000))
        assertEquals(1000, DialogLayoutRules.snapHeight(999, false, 400, 1000))
        assertEquals(400, DialogLayoutRules.snapHeight(1000, false, 400, 1000))
        assertNull(DialogLayoutRules.snapHeight(400, false, 400, 1000))
    }
    @Test fun heightAnimationInterpolatesInBothDirectionsAndTruncatesFractions() {
        assertEquals(400, DialogLayoutRules.animatedHeight(400, 1000, 0f))
        assertEquals(700, DialogLayoutRules.animatedHeight(400, 1000, .5f))
        assertEquals(1000, DialogLayoutRules.animatedHeight(400, 1000, 1f))
        assertEquals(700, DialogLayoutRules.animatedHeight(1000, 400, .5f))
        assertEquals(0, DialogLayoutRules.animatedHeight(0, 1, .5f))
    }
}
