package coroutines.retrofit

import coroutines.dto.Author
import coroutines.dto.Comment
import coroutines.dto.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PostService {

    companion object {
        const val BASE_URL: String = "http://localhost:9999/api/"
    }

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("authors/{id}")
    suspend fun getAuthor(@Path("id") id: Long): Response<Author>

    @GET("posts/{post_id}/comments")
    suspend fun getComments(@Path("post_id") id: Long): Response<List<Comment>>
}