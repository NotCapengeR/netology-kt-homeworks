package format

private const val RED_TXT = "\u001B[31m"
private const val RESET_TXT = "\u001B[0m"

fun main() {
    while (true) {
        try {
            println("${RESET_TXT}Тест. Введите количество лайков (введите ноль, чтоб закрыть программу):${RESET_TXT}")
            val likes: Int = readLine()!!.trim().toInt()
            if (likes == 0) break
            if (likes < 0) throw IllegalArgumentException()
            println("Понравилось ${format(likes)}")
        } catch (ex: IllegalArgumentException) {
            println("${RED_TXT}Ошибка: Неверный ввод!${RED_TXT}")
        }
    }
    println("${RESET_TXT}Завршение работы...${RESET_TXT}")
}

private fun format(likes: Int): String {
    val format: String = when (likes % 10) {
        0 -> {
            if (likes < 1000) "человекам"
            else "человек"
        }
        1 -> {
            if (likes % 100 == 11) "человекам"
            else "человеку"
        }
        2, 3, 4 -> {
            if (likes % 100 in 12..14) "человекам"
            else "людям"
        }
        else -> "человекам"
    }
    return "$likes $format"
}