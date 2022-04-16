package post

import attachments.Attachments
import comments.Comment
import reports.Report
import java.time.LocalDateTime

data class Post(
    val id: Long,
    val wallOwnerId: Long,
    val authorId: Long,
    val authorName: String,
    val text: String,
    val date: LocalDateTime = LocalDateTime.now(),
    val attachments: MutableList<Attachments> = mutableListOf(),
    val comments: HashMap<Long, Comment> = HashMap(),
    val reports: HashMap<Long, Report> = HashMap(),
    val likes: Int = 0,
    val views: Int = 0,
    val copyright: String?,
    val replyOwnerId: Long?,
    val replyPostId: Long?,
    val isPinned: Boolean,
    val friendOnly: Boolean,
    val markedAsAd: Boolean,
    val editHistory: MutableList<String> = mutableListOf()
)
