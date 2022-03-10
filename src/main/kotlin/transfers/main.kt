package transfers

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

private const val RED_TXT = "\u001B[31m"
private const val RESET_TXT = "\u001B[0m"
private val decimalFormat = DecimalFormat("###.##")

fun main() {
    println("Enter negative or zero number to finish the program.")
    while (true) {
        try {
            println("${RESET_TXT}Enter the amount for transfer (in ₽):${RESET_TXT}")
            val transferred = BigDecimal(readLine()!!.trim().toDouble()).setScale(2, RoundingMode.HALF_DOWN)
            if (transferred <= BigDecimal(0)) break
            if (transferred <= BigDecimal(35)) {
                println("${RED_TXT}Not enough funds to transfer money!${RED_TXT}")
                continue
            }
            val commission: BigDecimal = commission(transferred)
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

private fun commission(amount: BigDecimal): BigDecimal {
    val commission: BigDecimal = amount * BigDecimal(0.0075)
    return if (commission > BigDecimal(35)) commission
    else BigDecimal(35)
}

private fun totalTransferred(transferred: BigDecimal, commission: BigDecimal): BigDecimal {
    val total: BigDecimal = transferred - commission
    return if (total.compareTo(BigDecimal(0)) == 1) {
        total
    } else BigDecimal(0)
}
