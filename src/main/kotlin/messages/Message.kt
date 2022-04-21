package wall.messages

import attachments.Attachments
import java.time.LocalDateTime

data class Message(
    val id: Long,
    val text: String,
    val authorId: Long,
    val date: LocalDateTime,
    val replyMessageId: Long?,
    val attachments: MutableList<Attachments> = mutableListOf(),
    val editHistory: MutableList<String> = mutableListOf(),
    val readIds: MutableList<Long> = mutableListOf() // id всех юзеров, прочитавших сообщение
)
