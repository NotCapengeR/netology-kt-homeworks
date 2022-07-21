package coroutines.retrofit

import coroutines.retrofit.NetworkResult.Companion.EXCEPTION_OCCURRED_CODE
import retrofit2.HttpException
import retrofit2.Response

sealed class NetworkResult<T>(
    open val data: T? = null,
    open val code: Int? = null,
    open val error: Throwable? = null,
    val status: NetworkStatus
) {

    data class Success<T>(
        override val data: T,
        override val code: Int
    ) : NetworkResult<T>(status = NetworkStatus.SUCCESS)

    data class Error<T>(
        override val error: Throwable,
        override val code: Int,
    ) : NetworkResult<T>(status = NetworkStatus.ERROR)

    class Loading<T> : NetworkResult<T>(status = NetworkStatus.LOADING)

    companion object {
        // HTTP response codes
        const val RESPONSE_CODE_CONTINUE: Int = 100
        const val RESPONSE_CODE_OK: Int = 200
        const val RESPONSE_CODE_CREATED: Int = 201
        const val RESPONSE_CODE_ACCEPTED: Int = 202
        const val RESPONSE_CODE_BAD_REQUEST: Int = 400
        const val RESPONSE_CODE_UNAUTHORIZED: Int = 401
        const val RESPONSE_CODE_PAYMENT_REQUIRED: Int = 402
        const val RESPONSE_CODE_FORBIDDEN: Int = 403
        const val RESPONSE_CODE_NOT_FOUND: Int = 404
        const val RESPONSE_CODE_METHOD_NOT_ALLOWED: Int = 405
        const val RESPONSE_CODE_NOT_ACCEPTABLE: Int = 406
        const val RESPONSE_CODE_BAD_GATEWAY: Int = 502
        const val RESPONSE_CODE_TIMEOUT_GATEWAY: Int = 504

        // Custom codes
        const val EXCEPTION_OCCURRED_CODE: Int = -1
        const val EMPTY_CODE: Int = 0
    }
}

enum class NetworkStatus(val codesRange: IntRange?) {
    SUCCESS(200..300),
    ERROR(400..500),
    LOADING(null)
}

class FailedHttpRequestException(response: Response<*>) : HttpException(response)

suspend fun <T> saveApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    try {
        val response = apiCall.invoke()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return NetworkResult.Success(body, response.code())
            }
        }
        return NetworkResult.Error(FailedHttpRequestException(response), response.code())
    } catch (t: Throwable) {
        return NetworkResult.Error(t, EXCEPTION_OCCURRED_CODE)
    }
}