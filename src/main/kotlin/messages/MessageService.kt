package wall.messages

import attachments.Attachments

interface MessageService {

    fun getMessageById(messageId: Long, firstUserId: Long, secondUserId: Long): Message?

    fun getDialogsByUserId(userId: Long): List<Dialog>

    fun getUnreadDialogs(userId: Long): List<Dialog>

    fun deleteDialog(deleterId: Long, secondUserId: Long): Boolean

    fun createDialog(firstUserId: Long, secondUserId: Long): Dialog

    fun sendMessage(
        senderId: Long,
        recipientId: Long,
        text: String,
        replyMessageId: Long? = null,
        attachments: List<Attachments>? = null
    ): Long

    fun editMessage(editorId: Long, secondUserId: Long, messageId: Long, newText: String): Boolean

    fun deleteMessage(editorId: Long, secondUserId: Long, messageId: Long): Boolean

    fun getMessages(firstUserId: Long, secondUserId: Long, offset: Int, count: Int, vararg messageId: Long): List<Message>

    fun attach(message: Message, attachments: List<Attachments>)

    fun read(messageId: Long, readerId: Long, secondUserId: Long): List<Message>
}