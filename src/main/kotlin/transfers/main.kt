package transfers

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

private const val RED_TXT = "\u001B[31m"
private const val RESET_TXT = "\u001B[0m"
private val decimalFormat = DecimalFormat("###.##")
private val transferSystems = listOf(
    TransferSystem.Mastercard,
    TransferSystem.Maestro,
    TransferSystem.Visa,
    TransferSystem.MIR,
    TransferSystem.VKPay
)

enum class TransferSystem {
    Mastercard,
    Maestro,
    Visa,
    MIR,
    VKPay
}

fun main() {
    val transferSumInMonth = mutableListOf<BigDecimal>()
    repeat(transferSystems.size) { // Для каждой системы свой счётчик месячных лимитов.
        transferSumInMonth.add(BigDecimal(0))
    }
    println("Enter zero number to finish the program")
    while (true) {
        try {
            println("${RESET_TXT}Choose your transfer system:\n1) Mastercard\n2) Maestro\n3) Visa\n4) MIR\n5) VK Pay${RESET_TXT}")
            val inputTransferSystem: Int = readLine()!!.trim().toInt()
            when (inputTransferSystem) {
                0 -> break
                !in 1..transferSystems.size -> {
                    println("${RED_TXT}Error: Transfer system you entered does not exist!${RED_TXT}")
                    continue
                }
            }
            val transferSystem: TransferSystem = transferSystems[inputTransferSystem - 1]
            println("Enter the amount for transfer (in ₽)")
            val transferred = BigDecimal(readLine()!!.trim().toDouble()).setScale(2, RoundingMode.HALF_DOWN)
            if (!checkLimits(transferred, transferSumInMonth[inputTransferSystem - 1], transferSystem)) continue

            val commission: BigDecimal =
                commission(transferred, transferSumInMonth[inputTransferSystem - 1], transferSystem)
            transferSumInMonth[inputTransferSystem - 1] += totalTransferred(transferred, commission)
            println(
                "Input amount for transfer: ${decimalFormat.format(transferred)} ₽.\n" +
                        "Commission: ${decimalFormat.format(commission)} ₽.\n" +
                        "Total transferred: ${decimalFormat.format(totalTransferred(transferred, commission))} ₽.\n"
            )
        } catch (ex: IllegalArgumentException) {
            println("${RED_TXT}Error: Invalid input data!${RED_TXT}")
        }
    }
    println("Finishing the program...")
}

@JvmOverloads
fun commission(
    amount: BigDecimal,
    transferSumInMonth: BigDecimal = BigDecimal(0), // По сути здесь не имеет смысла добавление значений по умолчанию
    transferSystem: TransferSystem = TransferSystem.VKPay // Но в задании написано, что надо, значит надо :)
): BigDecimal {
    return when (transferSystem) {
        TransferSystem.VKPay -> BigDecimal(0)
        TransferSystem.Visa, TransferSystem.MIR -> {
            val commission: BigDecimal = amount * BigDecimal(0.0075)
            if (commission > BigDecimal(35)) commission.setScale(2, RoundingMode.HALF_DOWN)
            else BigDecimal(35)
        }
        TransferSystem.Maestro, TransferSystem.Mastercard -> {
            if (transferSumInMonth + amount <= BigDecimal(75000)) BigDecimal(0)
            else (amount * BigDecimal(0.006) + BigDecimal(20)).setScale(2, RoundingMode.HALF_DOWN)
        }
    }
}

fun checkLimits(amount: BigDecimal, transferSumInMonth: BigDecimal, transferSystem: TransferSystem): Boolean {
    if (amount <= BigDecimal(35) && (transferSystem == TransferSystem.MIR || transferSystem == TransferSystem.Visa)) {
        println("${RED_TXT}Not enough funds to transfer money (you need 35 ₽ to pay commission)!${RED_TXT}")
        return false
    }

    if (transferSystem == TransferSystem.VKPay &&
        (amount > BigDecimal(15_000) || transferSumInMonth + amount > BigDecimal(40_000))
    ) {
        println("${RED_TXT}You have exceeded the allowable limits for this month or this transaction!${RED_TXT}")
        return false
    }
    if (transferSystem != TransferSystem.VKPay &&
        (amount > BigDecimal(150_000) || transferSumInMonth + amount > BigDecimal(600_000))
    ) {
        // Здесь сутки я посчитал за один раз
        println("${RED_TXT}You have exceeded the allowable limits for this month or this transaction!${RED_TXT}")
        return false
    }
    return true
}

fun totalTransferred(transferred: BigDecimal, commission: BigDecimal): BigDecimal {
    val total: BigDecimal = transferred - commission
    return if (total.compareTo(BigDecimal(0)) == 1) {
        total
    } else BigDecimal(0)
}
