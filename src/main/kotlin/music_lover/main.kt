package music_lover

import java.math.BigDecimal
import java.math.RoundingMode

private const val RED_TXT = "\u001B[31m"
private const val RESET_TXT = "\u001B[0m"
private val products = HashMap<Int, BigDecimal>()
private var isMusicLover = false
private var lastPurchase: BigDecimal? = BigDecimal(0)

fun main() {
    products[1] = BigDecimal(120.18)
    products[2] = BigDecimal(2000.12)
    products[3] = BigDecimal(15000.25)
    products[4] = BigDecimal(1500)
    products[5] = BigDecimal(300.50)
    products[6] = BigDecimal(100)
    products[7] = BigDecimal(3000.34)
    println("Введите «0», чтоб закрыть программу")
    while (true) {
        try {
            var count = 0
            println("${RESET_TXT}Список товаров, доступных для покупки:${RESET_TXT}")
            products.forEach {
                println("${it.key}) Покупка на сумму ${priceFormat(it.value)}")
            }
            val product: Int = readLine()!!.trim().toInt()
            if (product == 0) break
            if (products.containsKey(product)) {
                println("Совершена покупка на сумму ${products[product]?.let { priceFormat(it) }}")
                lastPurchase = products[product]
                count++
                if (count == 3) isMusicLover = true
            } else {
                println("${RED_TXT}Такого товара нет в наличии!${RED_TXT}")
                continue
            }
        } catch (ex: IllegalArgumentException) {
            println("${RED_TXT}Такого товара нет в наличии!${RED_TXT}")
        }
    }
    println("Завершение работы...")
}

private fun priceFormat(price: BigDecimal): String {
    val priceWithDiscount = discount(price, lastPurchase, isMusicLover)
    val strPrice: List<String> = priceWithDiscount.toString().split(".")
    val prices: StringBuilder =
        StringBuilder("${strPrice[0]} ${caseFormat(strPrice[0], "рубль", "рубля", "рублей")}")
    if (strPrice[1] != "00") prices.append(
        ", ${strPrice[1].replace("^0+".toRegex(), "")} ${
            caseFormat(
                strPrice[1],
                "копейку",
                "копейки",
                "копеек"
            )
        }"
    )
    return prices.toString()
}

private fun caseFormat(number: String, one: String, fromTwoToFour: String, others: String): String {
    if (number.toInt() in 11..14) return others
    return when (number.toInt() % 10) {
        1 -> one
        in 2..4 -> fromTwoToFour
        else -> others
    }
}

private fun discount(price: BigDecimal, lastPurchase: BigDecimal?, isMusicLover: Boolean): BigDecimal {
    var discount = BigDecimal(0)
    if (lastPurchase!! in BigDecimal(1001)..BigDecimal(10000)) {
        discount += price.subtract(BigDecimal(100)).setScale(2, RoundingMode.HALF_DOWN)
    } else if (lastPurchase >= BigDecimal(10001)) {
        discount += price.multiply(BigDecimal(0.05)).setScale(2, RoundingMode.HALF_DOWN)
    }
    if (isMusicLover) {
        discount += (price - discount).multiply(BigDecimal(0.01)).setScale(2, RoundingMode.HALF_DOWN)
    }
    return (price - discount).setScale(2, RoundingMode.HALF_DOWN)
}

