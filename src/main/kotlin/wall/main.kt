package wall

import attachments.Attachments
import attachments.Video
import attachments.VideoAttachment
import kotlin.collections.HashMap

fun main() {
    val valera = User(1, "Valera")
    val kirkorov = User(2, "Valera")
    val video = Video(1, "Type name", "mp4", kirkorov.id, kirkorov.id, kirkorov.name, 2073600)
    val attachVideo = VideoAttachment(video = video)
    val video2 = Video(2, "Type name2", "mp4", kirkorov.id, kirkorov.id, kirkorov.name, 2073600)
    val attachVideo2: Attachments = VideoAttachment(video = video2)

    WallService.addPost("test0", valera, valera)
    WallService.addPost("test1", valera, valera)
    WallService.addPost("test2", valera, valera)
    WallService.outputUserWall(valera)
    WallService.updatePost(1, "test4", valera)
    WallService.attach(1, valera, attachVideo)
    WallService.attach(1, valera, attachVideo2)
    WallService.outputAttachments(1)
    println()
    WallService.outputUserWall(valera)
    println()
    WallService.deletePost(2)
    WallService.outputUserWall(valera)
}

object WallService {
    private val posts = HashMap<Long, HashMap<Long, Post>>()
    private var id = 1L

    fun outputUserWall(user: User) {
        posts[user.id]?.values?.forEach {
            println(it) // Лень было toString() переопределять для красивого вывода)
        }               // Энивей для нормального отображения будет UI использоваться
    }

    fun outputAttachments(postId: Long) {
        val post = findPostById(postId)
        println("Attachments: ${post?.attachments}")
    }

    @JvmOverloads
    fun addPost(
        postText: String,
        wallOwner: User,
        author: User,
        copyright: String? = null,
        replyPost: Post? = null,
        isPinned: Boolean = false ,
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
        )

        if (!posts.containsKey(wallOwner.id)) {
            posts[wallOwner.id] = HashMap()
        }
        posts[wallOwner.id]?.put(post.id, post)
        id++
        return posts[wallOwner.id]?.containsValue(post) == true
    }

    fun updatePost(postId: Long, newText: String, updateAuthor: User): Boolean {
        val post = findPostById(postId)
        if (post != null && updateAuthor.id == post.authorId) {
            val newPost = post.copy(text = newText)
            newPost.editHistory.add(post.text)
            val postsList = posts[post.wallOwnerId]
            postsList?.set(postId, newPost)
            return posts[post.wallOwnerId]?.containsValue(newPost) == true
        }
        return false
    }

    fun deletePost(postId: Long): Boolean {
        val post = findPostById(postId)
        if (post != null) {
            val postsList = posts[post.wallOwnerId]
            postsList?.remove(post.id)
            return postsList?.containsValue(post) == false
        }
        return false
    }

    fun attach(postId: Long, attachAuthor: User, attachment: Attachments): Boolean {
        val post = findPostById(postId)
        if (post != null && attachAuthor.id == post.authorId) {
            post.attachments.add(attachment)
            return post.attachments.contains(attachment)
        }
        return false
    }

    private fun findPostById(postId: Long): Post? {
        posts.values.forEach {
            if (it.containsKey(postId)) return it[postId]
        }
        return null
    }
}

class WallServiceForTests {
    private val posts = HashMap<Long, HashMap<Long, Post>>()
    private var id = 1L

    @JvmOverloads
    fun addPost(
        postText: String,
        wallOwner: User,
        author: User,
        copyright: String? = null,
        replyPost: Post? = null,
        isPinned: Boolean = false ,
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
        )

        if (!posts.containsKey(wallOwner.id)) {
            posts[wallOwner.id] = HashMap()
        }
        posts[wallOwner.id]?.put(post.id, post)
        id++
        return posts[wallOwner.id]?.containsValue(post) == true
    }

    fun updatePost(postId: Long, newText: String, updateAuthor: User): Boolean {
        val post = findPostById(postId)
        if (post != null && updateAuthor.id == post.authorId) {
            val newPost = post.copy(text = newText)
            newPost.editHistory.add(post.text)
            val postsList = posts[post.wallOwnerId]
            postsList?.set(postId, newPost)
            return posts[post.wallOwnerId]?.containsValue(newPost) == true
        }
        return false
    }

    fun deletePost(postId: Long): Boolean {
        val post = findPostById(postId)
        if (post != null) {
            val postsList = posts[post.wallOwnerId]
            postsList?.remove(post.id)
            return postsList?.containsValue(post) == false
        }
        return false
    }

    fun findPostById(postId: Long): Post? {
        posts.values.forEach {
            if (it.containsKey(postId)) return it[postId]
        }
        return null
    }
}