package wall.messages

import attachments.Attachments
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min

object MessageServiceImpl : MessageService {
    private val dialogs = HashMap<Pair<Long, Long>, Dialog>()
    private var messageId: Long = 1L

    private fun getKey(firstUserId: Long, secondUserId: Long): Pair<Long, Long> {
        val min = min(firstUserId, secondUserId)
        val max = max(firstUserId, secondUserId)
        return Pair(min, max)
    }

    private fun searchMessage(
        firstUserId: Long,
        secondUserId: Long,
        offset: Int = 0,
        sort: Boolean = true,
        count: Int = 20,
        vararg messageIds: Long,
    ): MessageSearchResult {
        val key = getKey(firstUserId, secondUserId)
        val dialog = dialogs[key]
        if (dialog != null) {
            val messages = dialog.messages.values
            messages.filter { messageIds.contains(it.id) && messages.indexOf(it) >= offset }
                .ifEmpty { return MessageSearchResult.NotFound }
                .take(count)
                .sortedWith { p1, p2 ->
                    p1.date.compareTo(p2.date)
                }
                .let {
                    return if (!sort) MessageSearchResult.Success(it.reversed()) else MessageSearchResult.Success(it)
                }
        }
        return MessageSearchResult.NotFound
    }

    private fun searchDialogs(
        firstUserId: Long,
        secondUserId: Long? = null,
        offset: Int = 0,
        sort: Boolean = true,
        count: Int = 20,
    ): DialogSearchResult {
        if (secondUserId != null) { // всегда 1 диалог
            val key = getKey(firstUserId, secondUserId)
            val dialog = dialogs[key]
            return if (dialog != null) {
                DialogSearchResult.Success(listOf(dialog))
            } else DialogSearchResult.NotFound
        }
        val dialogs = dialogs.values
        dialogs.filter { (it.id.first == firstUserId || it.id.second == firstUserId) && dialogs.indexOf(it) >= offset }
            .ifEmpty { return DialogSearchResult.NotFound }
            .take(count)
            .sortedWith { p1, p2 ->
                p1.creationDate.compareTo(p2.creationDate)
            }
            .let {
                return if (!sort) DialogSearchResult.Success(it.reversed()) else DialogSearchResult.Success(it)
            }
    }

    override fun getMessageById(messageId: Long, firstUserId: Long, secondUserId: Long): Message? {
        val result = searchMessage(
            firstUserId = firstUserId,
            secondUserId = secondUserId,
            count = 1,
            messageIds = longArrayOf(messageId)
        )
        return when (result) {
            is MessageSearchResult.Success -> result.messages.first()
            is MessageSearchResult.Failure -> null
            is MessageSearchResult.NotFound -> null
        }
    }

    override fun getDialogsByUserId(userId: Long): List<Dialog> =
        when (val result = searchDialogs(firstUserId = userId, count = dialogs.size)) {
            is DialogSearchResult.Success -> result.dialogs
            is DialogSearchResult.Failure -> emptyList()
            is DialogSearchResult.NotFound -> emptyList()
        }

    override fun getUnreadDialogs(userId: Long): List<Dialog> =
        when (val result = searchDialogs(firstUserId = userId, count = dialogs.size)) {
            is DialogSearchResult.Success -> {
                result.dialogs.filter {
                    !it.messages.values.last().readIds.contains(userId)
                }
            }
            is DialogSearchResult.Failure -> emptyList()
            is DialogSearchResult.NotFound -> emptyList()
        }

    override fun createDialog(firstUserId: Long, secondUserId: Long): Dialog {
        val key = getKey(firstUserId, secondUserId)
        if (dialogs[key] == null) {
            val dialog = Dialog(key, HashMap(), LocalDateTime.now())
            dialogs[key] = dialog
            return dialog
        }
        throw DialogAlreadyExistException()
    }

    override fun deleteDialog(deleterId: Long, secondUserId: Long): Boolean =
        when (val result = searchDialogs(firstUserId = deleterId, secondUserId = secondUserId, count = 1)) {
            is DialogSearchResult.Success -> {
                dialogs.remove(result.dialogs.first().id)
                !dialogs.containsKey(result.dialogs.first().id)
            }
            is DialogSearchResult.Failure -> false
            is DialogSearchResult.NotFound -> false
        }

    override fun sendMessage(
        senderId: Long,
        recipientId: Long,
        text: String,
        replyMessageId: Long?,
        attachments: List<Attachments>?
    ): Long {
        val message = Message(
            id = messageId,
            authorId = senderId,
            text = text,
            date = LocalDateTime.now(),
            replyMessageId = replyMessageId
        )
        message.readIds.add(senderId)
        val key = getKey(senderId, recipientId)
        val searchResult = searchDialogs(senderId, recipientId)
        if (searchResult is DialogSearchResult.NotFound) {
            dialogs[key] = createDialog(senderId, recipientId)
        }
        dialogs[key]?.messages?.set(messageId, message)
        messageId++
        if (attachments != null) attach(message, attachments)
        return message.id
    }

    override fun editMessage(editorId: Long, secondUserId: Long, messageId: Long, newText: String): Boolean {
        val message = getMessageById(messageId, editorId, secondUserId)
        if (message != null && message.authorId == editorId) {
            val newMessage = message.copy(text = newText)
            newMessage.editHistory.add(message.text)
            val key = getKey(editorId, secondUserId)
            dialogs[key]?.messages?.set(messageId, newMessage)
            return dialogs[key]?.messages?.containsValue(newMessage) == true
        }
        return false
    }

    override fun deleteMessage(editorId: Long, secondUserId: Long, messageId: Long): Boolean {
        val message = getMessageById(messageId, editorId, secondUserId)
        if (message != null) {
            val key = getKey(editorId, secondUserId)
            dialogs[key]?.messages?.remove(messageId)
            return dialogs[key]?.messages?.containsValue(message) == false
        }
        return false
    }

    override fun getMessages(
        firstUserId: Long,
        secondUserId: Long,
        offset: Int,
        count: Int,
        vararg messageId: Long
    ): List<Message> {
        val searchResult = searchMessage(
            firstUserId = firstUserId,
            secondUserId = secondUserId,
            offset = offset,
            count = count,
            messageIds = messageId
        )
        if (searchResult is MessageSearchResult.Success) {
            return searchResult.messages
        }
        return emptyList()
    }

    override fun read(messageId: Long, readerId: Long, secondUserId: Long): List<Message> {
        val dialog = searchDialogs(firstUserId = readerId,secondUserId = secondUserId, count = 1).dialogs?.first()
        val message = getMessageById(messageId, readerId, secondUserId)
        if (dialog?.messages?.containsValue(message) == true && message?.readIds?.contains(readerId) == false) {
            val unreadMessage = dialog.messages.values
                .filter { it.id <= messageId }
                .takeWhile { !it.readIds.contains(readerId) }
            unreadMessage.forEach {
                it.readIds.add(readerId)
                return unreadMessage
            }
        }
        return emptyList()
    }

    override fun attach(message: Message, attachments: List<Attachments>) {
        attachments.forEach {
            if (message.attachments.size >= 10) return
            message.attachments.add(it)
        }
    }
}