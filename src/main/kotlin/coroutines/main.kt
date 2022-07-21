package coroutines

import com.google.gson.GsonBuilder
import coroutines.retrofit.PostService
import coroutines.retrofit.saveApiCall
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val gson = GsonBuilder().create()

private val client = OkHttpClient.Builder()
    .connectTimeout(30L, TimeUnit.SECONDS)
    .readTimeout(30L, TimeUnit.SECONDS)
    .writeTimeout(30L, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor(::println))
    .build()

private val service: PostService = Retrofit.Builder()
    .baseUrl(PostService.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .client(client)
    .build()
    .create(PostService::class.java)

private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

fun main() {
    scope.launch {

        val posts = saveApiCall { service.getPosts() }
        val result = posts.data?.onEach { post ->
            val author = saveApiCall { service.getAuthor(post.authorId) }.data
            if (author != null) {
                post.author = author
            }
            val comments = saveApiCall { service.getComments(post.id) }.data
            if (!comments.isNullOrEmpty()) {
                post.comments = comments.onEach { comment ->
                    val commentAuthor = saveApiCall { service.getAuthor(comment.authorId) }.data
                    if (commentAuthor!= null) {
                        comment.author = commentAuthor
                    }
                }
            }
        }

        println(gson.toJson(result))
    }
    Thread.sleep(100L)
}
