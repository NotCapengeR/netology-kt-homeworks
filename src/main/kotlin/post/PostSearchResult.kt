package post

sealed class PostSearchResult(open val post: Post?) {
    data class Success(override val post: Post) : PostSearchResult(post)
    object PostNotFound : PostSearchResult(null)
}