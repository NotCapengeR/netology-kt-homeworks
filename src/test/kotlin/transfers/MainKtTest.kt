package transfers

import junit.framework.TestCase.assertEquals
import org.junit.Test

import java.math.BigDecimal
import java.math.RoundingMode

internal class MainKtTest {

    @Test
    fun `check limits mir commission`() {
        val amount = BigDecimal(34)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.MIR
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(false, limits)
    }

    @Test
    fun `check limits visa commission`() {
        val amount = BigDecimal(35)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.Visa
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(false, limits)
    }

    @Test
    fun `check limits visa`() {
        val amount = BigDecimal(10_000)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.Visa
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(true, limits)
    }

    @Test
    fun `check limits vk pay`() {
        val amount = BigDecimal(15_000)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.VKPay
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(true, limits)
    }

    @Test
    fun `check limits vk pay max transfer`() {
        val amount = BigDecimal(15_001)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.VKPay
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(false, limits)
    }

    @Test
    fun `check limits vk pay max in month`() {
        val amount = BigDecimal(15_000)
        val totalSumInMonth = BigDecimal(25_001)
        val transferSystem = TransferSystem.VKPay
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(false, limits)
    }

    @Test
    fun `check limits card max transfer`() {
        val amount = BigDecimal(150_000.1)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.Visa
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(false, limits)
    }

    @Test
    fun `check limits card max in month`() {
        val amount = BigDecimal(150_000)
        val totalSumInMonth = BigDecimal(450_001)
        val transferSystem = TransferSystem.Visa
        val limits = checkLimits(amount, totalSumInMonth, transferSystem)
        assertEquals(false, limits)
    }

    @Test
    fun `total transferred`() {
        val transferred = BigDecimal(36)
        val commission = BigDecimal(35)
        val total = totalTransferred(transferred, commission)
        assertEquals(BigDecimal(1), total)
    }

    @Test
    fun `total transferred with too big commission`() {
        val transferred = BigDecimal(36)
        val commission = BigDecimal(100)
        val total = totalTransferred(transferred, commission)
        assertEquals(BigDecimal(0), total)
    }

    @Test
    fun `commission default`() {
        val amount = BigDecimal(15_000)
        val commission = commission(amount)
        assertEquals(BigDecimal(0), commission)
    }

    @Test
    fun `commission mir`() {
        val amount = BigDecimal(10_000)
        val transferSystem = TransferSystem.MIR
        val totalSumInMonth = BigDecimal(0)
        val expected = BigDecimal(75).setScale(2, RoundingMode.HALF_DOWN)
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(expected, commission)
    }

    @Test
    fun `commission visa`() {
        val amount = BigDecimal(10_000)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.Visa
        val expected = BigDecimal(75).setScale(2, RoundingMode.HALF_DOWN)
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(expected, commission)
    }

    @Test
    fun `commission mir min`() {
        val amount = BigDecimal(100)
        val transferSystem = TransferSystem.MIR
        val totalSumInMonth = BigDecimal(0)
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(BigDecimal(35), commission)
    }

    @Test
    fun `commission visa min`() {
        val amount = BigDecimal(100)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.Visa
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(BigDecimal(35), commission)
    }

    @Test
    fun `commission mastercard default`() {
        val amount = BigDecimal(10_000)
        val transferSystem = TransferSystem.Mastercard
        val totalSumInMonth = BigDecimal(0)
        val expected = BigDecimal(0)
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(expected, commission)
    }

    @Test
    fun `commission maestro default`() {
        val amount = BigDecimal(10_000)
        val totalSumInMonth = BigDecimal(0)
        val transferSystem = TransferSystem.Maestro
        val expected = BigDecimal(0)
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(expected, commission)
    }

    @Test
    fun `commission mastercard with month limit`() {
        val amount = BigDecimal(10_000)
        val transferSystem = TransferSystem.Mastercard
        val totalSumInMonth = BigDecimal(75_000)
        val expected = BigDecimal(80).setScale(2, RoundingMode.HALF_DOWN)
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(expected, commission)
    }

    @Test
    fun `commission maestro with month limit`() {
        val amount = BigDecimal(10_000)
        val totalSumInMonth = BigDecimal(75_000)
        val transferSystem = TransferSystem.Maestro
        val expected = BigDecimal(80).setScale(2, RoundingMode.HALF_DOWN)
        val commission = commission(amount, totalSumInMonth, transferSystem)
        assertEquals(expected, commission)
    }
}