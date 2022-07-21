package coroutines.dto

import com.google.gson.annotations.Expose

data class Comment(
    @Expose val id: Long,
    @Expose val postId: Long,
    @Expose val authorId: Long,
    @Expose val content: String,
    @Expose val published: Long,
    @Expose val likedByMe: Boolean,
    @Expose val likes: Int = 0,
    var author: Author = NULL_AUTHOR,
) {
    companion object {
        val NULL_AUTHOR = Author(-1L, "", "")
    }
}