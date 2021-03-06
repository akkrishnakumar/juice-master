import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class JuiceMasterTest {

    val noMotionTimeout: Long = 100
    val isNightShift: NightShift = { true }
    val juiceMaster = JuiceMaster(
        floors = 2,
        mainCorridorsPerFloor = 1,
        subCorridorsPerFloor = 2,
        isNightShift = isNightShift,
        noMotionTimeout = noMotionTimeout
    )

    @Test
    internal fun `should switch on all main corridor light during night shift`() {
        val expectedStatus = sampleDefaultStatusAtNightShift

        val actualStatus = juiceMaster.status()

        assertThat(actualStatus, equalTo(expectedStatus))
    }

    @Test
    internal fun `should switch off all main corridor light during morning shift`() {
        val expectedStatus = sampleDefaultStatusAtMorningShift
        val isMorningShift: NightShift = { false }
        val juiceMasterWithMorningShift =
            JuiceMaster(2, 1, 2, isNightShift = isMorningShift)

        val actualStatus = juiceMasterWithMorningShift.status()

        assertThat(actualStatus, equalTo(expectedStatus))
    }

    @Test
    internal fun `should switch on light in sub corridor during night shift when motion is detected`() {
        // TODO: Need to change data where budgeting is not applied
        val expectedStatus = sampleStatusAtNightShiftWhenMotionDetected

        juiceMaster.consume(sampleMotionSignal)
        val actualStatus = juiceMaster.status()

        assertThat(actualStatus, equalTo(expectedStatus))
    }

    @Test
    internal fun `should switch off light and turn on ACs in sub corridor when no motion after a min`() {
        val expectedStatus = sampleStatusWhenNoMotionDetected

        juiceMaster.consume(sampleMotionSignal)
        simulateWaitingForAMinute()

        val actualStatus = juiceMaster.status()

        assertThat(actualStatus, equalTo(expectedStatus))
    }

    private fun simulateWaitingForAMinute() = Thread.sleep(noMotionTimeout + 50)

    val sampleDefaultStatusAtNightShift = """
        Floor 1
        Main corridor 1 Light 1 : ON AC : ON
        Sub corridor 1 Light 1 : OFF AC : ON
        Sub corridor 2 Light 2 : OFF AC : ON
        Floor 2
        Main corridor 1 Light 1 : ON AC : ON
        Sub corridor 1 Light 1 : OFF AC : ON
        Sub corridor 2 Light 2 : OFF AC : ON
    """.trimIndent()

    val sampleDefaultStatusAtMorningShift = """
        Floor 1
        Main corridor 1 Light 1 : OFF AC : ON
        Sub corridor 1 Light 1 : OFF AC : ON
        Sub corridor 2 Light 2 : OFF AC : ON
        Floor 2
        Main corridor 1 Light 1 : OFF AC : ON
        Sub corridor 1 Light 1 : OFF AC : ON
        Sub corridor 2 Light 2 : OFF AC : ON
    """.trimIndent()

    val sampleStatusAtNightShiftWhenMotionDetected = """
        Floor 1
        Main corridor 1 Light 1 : ON AC : ON
        Sub corridor 1 Light 1 : OFF AC : OFF
        Sub corridor 2 Light 2 : ON AC : ON
        Floor 2
        Main corridor 1 Light 1 : ON AC : ON
        Sub corridor 1 Light 1 : OFF AC : ON
        Sub corridor 2 Light 2 : OFF AC : ON
    """.trimIndent()

    val sampleStatusWhenNoMotionDetected = """
        Floor 1
        Main corridor 1 Light 1 : ON AC : ON
        Sub corridor 1 Light 1 : OFF AC : ON
        Sub corridor 2 Light 2 : OFF AC : ON
        Floor 2
        Main corridor 1 Light 1 : ON AC : ON
        Sub corridor 1 Light 1 : OFF AC : ON
        Sub corridor 2 Light 2 : OFF AC : ON
    """.trimIndent()


}