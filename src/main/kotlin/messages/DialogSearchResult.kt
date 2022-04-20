package wall.messages

sealed class DialogSearchResult(open val dialogs: List<Dialog>?) {
    data class Success(override val dialogs: List<Dialog>) : DialogSearchResult(dialogs)
    data class Failure(override val dialogs: List<Dialog>, val errorCode: Int) : DialogSearchResult(dialogs)
    object NotFound : DialogSearchResult(null)
}
