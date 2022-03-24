package wall

import kotlin.collections.HashMap


private const val RED_TXT = "\u001B[31m"
private const val RESET_TXT = "\u001B[0m"

fun main() {
    val valera = User(1, "Valera")

    WallService.addPost("test0", valera, valera)
    WallService.addPost("test1", valera, valera)
    WallService.addPost("test2", valera, valera)
    WallService.outputUserWall(valera)
    WallService.updatePost(1, "test4", valera)
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

    fun addPost(postText: String, wallOwner: User, author: User): Boolean { //Always true
        val post = Post(
            id = id,
            wallOwnerId = wallOwner.id,
            authorId = author.id,
            authorName = author.name,
            text = postText
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

    fun addPost(postText: String, wallOwner: User, author: User): Boolean { //Always true
        val post = Post(
            id = id,
            wallOwnerId = wallOwner.id,
            authorId = author.id,
            authorName = author.name,
            text = postText
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