package wall

import kotlin.collections.HashMap


private const val RED_TXT = "\u001B[31m"
private const val RESET_TXT = "\u001B[0m"

fun main() {
    val valera = User(1, "Valera")

    WallService.addPost("hueta0", valera, valera)
    WallService.addPost("hueta1", valera, valera)
    WallService.addPost("hueta2", valera, valera)
    WallService.outputUserWall(valera)
    WallService.updatePost(1, "hueta4")
    WallService.outputUserWall(valera)
}

object WallService {
    private val posts = HashMap<Long, MutableList<Post>>()
    private var id = 0L

    fun outputUserWall(user: User) {
        posts[user.id]?.forEach {
            println(it)
        }
    }

    fun addPost(postText: String, wallOwner: User, author: User) {
        val post = Post(
            id = id,
            wallOwnerId = wallOwner.id,
            authorId = author.id,
            text = postText
        )
        val currentUserPosts = mutableListOf<Post>()
        if (posts.containsKey(wallOwner.id)) {
            val previousUserPosts: List<Post>? = posts[wallOwner.id]
            previousUserPosts?.forEach {
                currentUserPosts.add(it)
            }
        }
        currentUserPosts.add(post)
        posts[wallOwner.id] = currentUserPosts
        id++
    }

    fun updatePost(postId: Long, newText: String) {
        val post = findPostById(postId)
        if (post != null) {
            val newPost = post.copy(text = newText)
            val postsList = posts[post.wallOwnerId]
            postsList?.set(postsList.indexOf(post), newPost)
        }
    }

    private fun findPostById(postId: Long): Post? {
        posts.forEach { it ->
            it.value.forEach {
                if (it.id == postId) return it
            }
        }
        return null
    }
}