package wall.messages

import java.time.LocalDateTime

data class Dialog(
    val id: Pair<Long, Long>, // id диалога — пара из двух id юзеров, где первое обязательно меньше второго
    val messages: HashMap<Long, Message>,
    val creationDate: LocalDateTime,
)
