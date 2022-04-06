package notes

import comments.Comment
import java.time.LocalDateTime

data class Note(
    val id: Long,
    val authorId: Long,
    val title: String,
    val text: String,
    val privacy: Int,
    val commentPrivacy: Int,
    val date: LocalDateTime = LocalDateTime.now(),
    val comments: HashMap<Long, Comment> = HashMap(),
    val deletedComments: HashMap<Long, Comment> = HashMap(),
)
