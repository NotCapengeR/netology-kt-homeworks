package comments

import attachments.Attachments
import reports.Report
import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val authorId: Long,
    val date: LocalDateTime,
    val text: String,
    val replyUserId: Long?,
    val replyCommentId: Long?,
    val reports: HashMap<Long, Report> = HashMap(),
    val attachments: MutableList<Attachments> = mutableListOf(),
    val parentsStack: List<Comment>? = null,
    val editHistory: MutableList<String> = mutableListOf()
)