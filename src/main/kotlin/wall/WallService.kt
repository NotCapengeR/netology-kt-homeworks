package wall.wall

import attachments.Attachments
import comments.Comment
import comments.CommentNotFoundException
import post.Post
import post.PostNotFoundException
import post.PostSearchResult
import reports.InvalidReasonException
import reports.Report
import wall.User
import java.time.LocalDateTime

object WallService {
    private val posts = HashMap<Long, HashMap<Long, Post>>()
    private var id = 1L
    private var commentId = 1L
    private var reportId = 1L
    private val reasons = makeReasons()

    private fun makeReasons(): HashMap<Int, String> {
        val reasons = HashMap<Int, String>()
        reasons[0] = "Спам"
        reasons[1] = "Детская порнография"
        reasons[2] = "Экстремизм"
        reasons[3] = "Пропаганда наркотиков"
        reasons[4] = "Насилие"
        reasons[5] = "Материал для взрослых"
        reasons[6] = "Оскорбление"
        reasons[8] = "Призыв к суициду"
        return reasons
    }

    fun outputUserWall(user: User) {
        posts[user.id]?.values?.forEach {
            println(it) // Лень было toString() переопределять для красивого вывода)
        }               // Энивей для нормального отображения будет UI использоваться
    }

    fun outputAttachments(postId: Long) {
        when (val result = findPostById(postId)) {
            is PostSearchResult.Success -> println("Attachments: ${result.post.attachments}")
            is PostSearchResult.PostNotFound -> println("Post not found!")
        }
    }

    fun report(commentId: Long, postId: Long, reason: Int): Boolean {
        when (val result = findPostById(postId)) {
            is PostSearchResult.Success -> {
                if (reasons.containsKey(reason)) {
                    val post = result.post
                    val report = reasons[reason]?.let { Report(id = reportId, commentId = commentId, reason = it) }
                    if (post.comments.containsKey(commentId) && report != null) {
                        post.comments[commentId]?.reports?.put(report.id, report)
                        return post.comments[commentId]?.reports?.containsValue(report) == true
                    } else throw CommentNotFoundException()
                } else throw InvalidReasonException()
            }
            is PostSearchResult.PostNotFound -> throw PostNotFoundException()
        }
    }

    @JvmOverloads
    fun createComment(
        text: String,
        authorId: Long,
        postId: Long,
        replyUserId: Long? = null,
        replyCommentId: Long? = null
    ) {
        when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                val comment = Comment(
                    id = commentId,
                    authorId = authorId,
                    text = text,
                    replyUserId = replyUserId,
                    replyCommentId = replyCommentId,
                    date = LocalDateTime.now(),
                )
                post.comments[commentId] = comment
                commentId++
            }
            is PostSearchResult.PostNotFound -> throw PostNotFoundException()
        }
    }

    @JvmOverloads
    fun addPost(
        postText: String,
        wallOwner: User,
        author: User,
        copyright: String? = null,
        replyPost: Post? = null,
        isPinned: Boolean = false,
        friendOnly: Boolean = false,
        markedAsAd: Boolean = false,
    ): Boolean { //Always true
        val post = Post(
            id = id,
            wallOwnerId = wallOwner.id,
            authorId = author.id,
            authorName = author.name,
            text = postText,
            copyright = copyright,
            date = LocalDateTime.now(),
            replyOwnerId = replyPost?.authorId,
            replyPostId = replyPost?.id,
            isPinned = isPinned,
            friendOnly = friendOnly,
            markedAsAd = markedAsAd,
        )

        if (!posts.containsKey(wallOwner.id)) {
            posts[wallOwner.id] = HashMap()
        }
        posts[wallOwner.id]?.put(post.id, post)
        id++
        return posts[wallOwner.id]?.containsValue(post) == true
    }

    fun updatePost(postId: Long, newText: String, updateAuthor: User): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                if (updateAuthor.id == post.authorId) {
                    val newPost = post.copy(text = newText)
                    newPost.editHistory.add(post.text)
                    val postsList = posts[post.wallOwnerId]
                    postsList?.set(postId, newPost)
                    postsList?.containsValue(newPost) == false
                } else false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    fun deletePost(postId: Long): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                val postsList = posts[post.wallOwnerId]
                postsList?.remove(post.id)
                postsList?.containsValue(post) == false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    fun attach(postId: Long, attachAuthor: User, attachment: Attachments): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                if (attachAuthor.id == post.authorId) {
                    post.attachments.add(attachment)
                    return post.attachments.contains(attachment)
                } else false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    private fun findPostById(postId: Long): PostSearchResult {
        posts.values.forEach {
            if (it.containsKey(postId) && it[postId] != null) return PostSearchResult.Success(it[postId]!!)
        }
        return PostSearchResult.PostNotFound
    }
}