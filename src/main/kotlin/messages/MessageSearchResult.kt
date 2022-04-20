package wall.messages

sealed class MessageSearchResult(open val messages: List<Message>?) {
    data class Success(override val messages: List<Message>) : MessageSearchResult(messages)
    data class Failure(override val messages: List<Message>, val errorCode: Int) : MessageSearchResult(messages)
    object NotFound : MessageSearchResult(null)
}
