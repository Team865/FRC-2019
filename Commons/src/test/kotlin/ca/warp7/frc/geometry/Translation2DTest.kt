package ca.warp7.frc.geometry

import org.junit.Test
import kotlin.test.assertEquals

class Translation2DTest {
    @Test
    fun magWorksProperly() {
        val trans = Translation2D(3.0, 4.0)
        assertEquals(trans.mag, 5.0)
    }
}
