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
    private val posts = HashMap<Long, MutableList<Post>>()
    private var id = 1L

    fun outputUserWall(user: User) {
        posts[user.id]?.forEach {
            println(it) // Лень было toString() переопределять для красивого вывода)
        }               // Энивей для нормального отображения будет UI использоваться
    }

    fun addPost(postText: String, wallOwner: User, author: User): Boolean { //Always true
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
        return true
    }

    fun updatePost(postId: Long, newText: String, updateAuthor: User): Boolean {
        val post = findPostById(postId)
        if (post != null && updateAuthor.id == post.authorId) {
            val newPost = post.copy(text = newText)
            val postsList = posts[post.wallOwnerId]
            postsList?.set(postsList.indexOf(post), newPost)
            return posts[post.wallOwnerId]?.contains(newPost)!!
        }
        return false
    }

    fun deletePost(postId: Long): Boolean {
        val post = findPostById(postId)
        if (post != null) {
            val postsList = posts[post.wallOwnerId]
            postsList?.remove(post)
            return !postsList?.remove(post)!!
        }
        return false
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

class WallServiceForTests {
    private val posts = HashMap<Long, MutableList<Post>>()
    private var id = 1L

    fun outputUserWall(user: User) {
        posts[user.id]?.forEach {
            println(it)
        }
    }

    fun addPost(postText: String, wallOwner: User, author: User): Boolean { //Always true
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
        return true
    }

    fun updatePost(postId: Long, newText: String, updateAuthor: User): Boolean {
        val post = findPostById(postId)
        if (post != null && updateAuthor.id == post.authorId) {
            val newPost = post.copy(text = newText)
            val postsList = posts[post.wallOwnerId]
            postsList?.set(postsList.indexOf(post), newPost)
            return posts[post.wallOwnerId]?.contains(newPost)!!
        }
        return false
    }

    fun deletePost(postId: Long): Boolean {
        val post = findPostById(postId)
        if (post != null) {
            val postsList = posts[post.wallOwnerId]
            postsList?.remove(post)
            return !postsList?.remove(post)!!
        }
        return false
    }

    fun findPostById(postId: Long): Post? {
        posts.forEach { it ->
            it.value.forEach {
                if (it.id == postId) return it
            }
        }
        return null
    }
}