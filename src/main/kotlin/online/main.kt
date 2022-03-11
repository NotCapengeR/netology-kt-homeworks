package online

data class User(val name: String, val gender: Gender, var lastSeen: Long)

enum class Gender {
    MALE,
    FEMALE
}

fun main() {
    val users = mutableListOf<User>()
    val smile = User("ヅ", Gender.MALE, 300)
    smile.lastSeen = 3000
    users.add(0, User("Валера", Gender.MALE, 2_119_100))
    users.add(1, User("Alex", Gender.MALE, 30))
    users.add(2, User("Вася", Gender.MALE, 3482))
    users.add(3, User("Олег", Gender.MALE, 10002))
    users.add(4, User("Женя :)", Gender.MALE, 700_000))
    users.add(5, User("Аркадий", Gender.MALE, 29_030_400))
    users.add(6, User("Crazy Frog", Gender.MALE, 172_800))
    users.add(7, User("Дональд Трамп", Gender.MALE, 29_030_401))
    users.add(8, smile)
    users.add(9, User("Пугачёва", Gender.FEMALE, 21_030_401))
    users.add(10, User("Ольга Бузова", Gender.FEMALE, 0))
    users[1].lastSeen = 120
    users.forEach {
        println("${it.name} ${if (it.gender == Gender.MALE) "был" else "была"} ${agoToText(it.lastSeen)}")
    }

}

private fun caseFormat(number: String, one: String, fromTwoToFour: String, others: String): String {
    if (number.toLong() in 11..14) return others
    return when (number.toLong() % 10) {
        1L -> one
        in 2..4 -> fromTwoToFour
        else -> others
    }
}

private fun agoToText(lastSeen: Long): String {
    if (lastSeen >= 0) {
        return when (lastSeen) {
            in 0..60 -> "только что"
            in 61..3600 -> "в сети ${
                caseFormat(
                    (lastSeen / 60).toString(),
                    "минуту",
                    "${(lastSeen / 60)} минуты",
                    "${(lastSeen / 60)} минут"
                )
            } назад"
            in 3601..86400 -> "в сети ${
                caseFormat(
                    (lastSeen / 3600).toString(),
                    "час",
                    "${(lastSeen / 3600)} часа",
                    "${(lastSeen / 3600)} часов"
                )
            } назад"
            in 86_401..172_800 -> "в сети сегодня"
            in 172_801..604_800 -> "в сети ${
                caseFormat(
                    (lastSeen / 172_800).toString(),
                    "вчера",
                    "${(lastSeen / 172_800)} дня назад",
                    "${(lastSeen / 172_800)} дней назад"
                )
            }"
            in 604_801..2_419_200 -> "в сети ${
                caseFormat(
                    (lastSeen / 604_800).toString(),
                    "неделю",
                    "${(lastSeen / 604_800)} недели",
                    "${(lastSeen / 604_800)} недель"
                )
            } назад"
            in 2_419_201..29_030_400 -> "в сети ${
                caseFormat(
                    (lastSeen / 2_419_200).toString(),
                    "месяц",
                    "${(lastSeen / 2_419_200)} месяца",
                    "${(lastSeen / 2_419_200)} месяцев"
                )
            } назад"
            else -> "в сети ${
                caseFormat(
                    lastSeen.toString(),
                    "год",
                    "$lastSeen года",
                    "$lastSeen лет"
                )
            } назад"
        }
    } else throw IllegalArgumentException("Error: Time could not be negative!")
}