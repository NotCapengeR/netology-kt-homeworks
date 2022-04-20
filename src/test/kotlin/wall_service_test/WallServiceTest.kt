package wall_service_test

import wall.*
import post.Post
import reports.Report
import comments.Comment
import post.PostSearchResult
import org.junit.jupiter.api.Test
import junit.framework.TestCase.*
import post.PostNotFoundException
import reports.InvalidReasonException
import comments.CommentNotFoundException
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class WallServiceTest {

    @Test
    fun `comment not found report`() {
        assertThrows<CommentNotFoundException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
            service.report(124, 1, 0)
        }
    }

    @Test
    fun `post not found report`() {
        assertThrows<PostNotFoundException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
            service.report(1, 3121, 0)
        }
    }

    @Test
    fun `invalid reason report`() {
        assertThrows<InvalidReasonException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
            service.report(1, 1, 132132)
        }
    }

    @Test
    fun `should throw`() {
        assertThrows<PostNotFoundException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 121)
        }
    }

    @Test
    fun `shouldn't throw`() {
        assertDoesNotThrow {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
        }
    }

    @Test
    fun `add check id`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("afskhjfjasfafhsja", owner, owner)
        val post = service.findPostById(0).post
        assertNull(post)
    }

    @Test
    fun `update existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.updatePost(2, "1lk23j13ljk2", owner)
        assertTrue(result)
    }

    @Test
    fun `update not existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.updatePost(10, "1lk23j13ljk2", owner)
        assertFalse(result)
    }

    @Test
    fun `delete existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.deletePost(2)
        assertTrue(result)
    }

    @Test
    fun `delete not existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.deletePost(10)
        assertFalse(result)
    }
}

class WallServiceForTests {
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
                    date = LocalDateTime.now()
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
            replyOwnerId = replyPost?.authorId,
            replyPostId = replyPost?.id,
            isPinned = isPinned,
            friendOnly = friendOnly,
            markedAsAd = markedAsAd,
            date = LocalDateTime.now()
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
                    postsList?.containsValue(newPost) == true
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

    fun findPostById(postId: Long): PostSearchResult {
        posts.values.forEach {
            if (it.containsKey(postId) && it[postId] != null) return PostSearchResult.Success(it[postId]!!)
        }
        return PostSearchResult.PostNotFound
    }
}