package coroutines.dto

import coroutines.dto.Comment.Companion.NULL_AUTHOR


data class Post(
    val id: Long,
    val authorId: Long,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment,
    var comments: List<Comment> = emptyList(),
    var author: Author = NULL_AUTHOR,
)
