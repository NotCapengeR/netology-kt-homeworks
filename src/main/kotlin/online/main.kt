package online

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class User(val name: String, val gender: Gender, var lastSeen: LocalDateTime)

enum class Gender {
    MALE,
    FEMALE
}

private val monthsAndYearsFormat = DateTimeFormatter.ofPattern("d MMMM yyyy в H:mm")
private val todayFormat = DateTimeFormatter.ofPattern("в H:mm")
private val absFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss")
private val weekFormat = DateTimeFormatter.ofPattern("EEEE в H:mm")

fun main() {
    val users = mutableListOf<User>()
    val smile = User("ヅ", Gender.MALE, LocalDateTime.parse("16-02-2022, 09:00:00", absFormat))
    smile.lastSeen = LocalDateTime.parse("07-08-2021, 09:00:00", absFormat)
    users.add(0, User("Валера", Gender.MALE, LocalDateTime.parse("16-03-2022, 09:00:00", absFormat)))
    users.add(1, User("Alex", Gender.MALE, LocalDateTime.parse("15-03-2022, 09:00:00", absFormat)))
    users.add(2, User("Вася", Gender.MALE, LocalDateTime.parse("15-03-2020, 09:00:00", absFormat)))
    users.add(3, User("Олег", Gender.MALE, LocalDateTime.parse("16-03-2022, 21:54:01", absFormat)))
    users.add(4, User("Женя :)", Gender.MALE, LocalDateTime.parse("07-08-2021, 09:00:00", absFormat)))
    users.add(5, User("Аркадий", Gender.MALE, LocalDateTime.parse("07-08-2021, 09:00:00", absFormat)))
    users.add(6, User("Crazy Frog", Gender.MALE, LocalDateTime.parse("07-08-2021, 09:00:00", absFormat)))
    users.add(7, User("Дональд Трамп", Gender.MALE, LocalDateTime.parse("07-08-2021, 09:00:00", absFormat)))
    users.add(8, smile)
    users.add(9, User("Пугачёва", Gender.FEMALE, LocalDateTime.parse("12-03-2022, 09:30:00", absFormat)))
    users.add(10, User("Ольга Бузова", Gender.FEMALE, LocalDateTime.parse("16-03-2022, 21:30:00", absFormat)))
    users[1].lastSeen = LocalDateTime.parse("15-03-2022, 03:00:00", absFormat)
    users.forEach {
        println("${it.name} ${if (it.gender == Gender.MALE) "был" else "была"} ${agoToText(it.lastSeen)}")
    }

}

private fun caseFormat(number: Long, one: String, fromTwoToFour: String, others: String): String {
    if (number in 11..14) return others
    return when (number % 10) {
        1L -> one
        in 2..4 -> fromTwoToFour
        else -> others
    }
}

private fun agoToText(lastSeen: LocalDateTime): String {
    val currentDate = LocalDateTime.now()
    val lastSeenToSeconds = ChronoUnit.SECONDS.between(lastSeen, currentDate)
    if (lastSeenToSeconds >= 0) {
        return when (lastSeenToSeconds) {
            in 0..60 -> "только что"
            in 61..3600 -> "в сети ${
                caseFormat(
                    (lastSeenToSeconds / 60),
                    "минуту",
                    "${(lastSeenToSeconds / 60)} минуты",
                    "${(lastSeenToSeconds / 60)} минут"
                )
            } назад"
            in 3601..36_000 -> "в сети ${
                caseFormat(
                    (lastSeenToSeconds / 3600),
                    "час",
                    "${(lastSeenToSeconds / 3600)} часа",
                    "${(lastSeenToSeconds / 3600)} часов"
                )
            } назад"
            in 36_001..86_400 -> "в сети ${if (lastSeen.dayOfYear != currentDate.dayOfYear) "вчера" else "сегодня"}" +
                    " ${todayFormat.format(lastSeen)}"
            in 86_401..604_800 -> "в сети ${
                when (lastSeenToSeconds / 86_400) {
                    1L -> "вчера ${todayFormat.format(lastSeen)}"
                    2L -> "позавчера ${todayFormat.format(lastSeen)}"
                    else -> accusativeDay(weekFormat.format(lastSeen))
                }
            }"
            else -> "в сети ${monthsAndYearsFormat.format(lastSeen)}"
        }
    } else throw IllegalArgumentException("Error: Invalid date (this is not a Back to Future)!")
}

private fun accusativeDay(day: String): String = day
    .replace("понедельник", "в понедельник")
    .replace("вторник", "во вторник")
    .replace("среда", "в среду")
    .replace("четверг", "в четверг")
    .replace("пятница", "в пятницу")
    .replace("суббота", "в субботу")
    .replace("воскресенье", "в воскресенье")