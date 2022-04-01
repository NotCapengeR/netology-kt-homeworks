package wall

sealed class PostSearchResult(open val post: Post?) {
    class Success(override val post: Post): PostSearchResult(post)
    object PostNotFound : PostSearchResult(null)
}