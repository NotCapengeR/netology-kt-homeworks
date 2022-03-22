package wall

import java.time.LocalDateTime

data class Post(
    val id: Long,
    val wallOwnerId: Long,
    val authorId: Long,
    val text: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val likes: Int = 0,
    val views: Int = 0,
    val replyOwnerId: Long? = null,
    val replyPostId: Long? = null,
    val friendOnly: Boolean = false,
)
