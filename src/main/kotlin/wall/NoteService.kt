package wall.wall

import notes.Note
import comments.Comment
import notes.NoteNotFoundException
import notes.UserNotFoundException
import java.time.LocalDateTime

private const val ALL_USERS = 1
private const val FRIENDS_ONLY = 2
private const val FRIENDS_AND_THEIR_FRIENDS = 3
private const val ONLY_YOU = 4

object NoteService {
    // first key — user id, second key — note id
    private val notes = HashMap<Long, HashMap<Long, Note>>()
    private var noteId = 1L
    private var guid = 1L

    private fun getNoteById(noteId: Long, ownerId: Long): Note {
        if (notes.containsKey(ownerId)) {
            return notes[ownerId]?.get(noteId) ?: throw NoteNotFoundException()
        } else throw UserNotFoundException()
    }

    @JvmOverloads
    fun getComments(
        noteId: Long,
        ownerId: Long,
        sort: Boolean = true, // false — сортировка в порядке убывания даты
        offset: Int = 0,
        count: Int = 20
    ): List<Comment> {
        if (count !in 1..100) {
            println("Count must be in the range [1; 100]!")
            return emptyList()
        }

        if (notes.containsKey(ownerId) && notes[ownerId]?.containsKey(noteId) == true) {
            val note = notes[ownerId]?.get(noteId)
            if (note != null && !note.comments.values.isEmpty()) {
                val commentsList = note.comments.values
                commentsList.filter { commentsList.indexOf(it) >= offset }
                    .take(count)
                    .sortedWith { p1, p2 ->
                        p1.date.compareTo(p2.date)
                    }
                    .let { return if (!sort) it.reversed() else it }
            }
        }
        return emptyList()
    }

    @JvmOverloads
    fun getNotes(
        ownerId: Long,
        sort: Boolean = true,
        offset: Int = 0,
        count: Int = 20,
        vararg noteIds: Long, // вообще это не есть правильно, но я просто хотел попробовать :D
    ): List<Note> {
        if (count !in 1..100) {
            println("Count must be in the range [1; 100]!")
            return emptyList()
        }

        if (!notes[ownerId]?.values.isNullOrEmpty() && !notes[ownerId].isNullOrEmpty()) {
            val notesList = notes[ownerId]?.values
            if (!notesList.isNullOrEmpty()) {
                notesList.filter { noteIds.contains(it.id) && notesList.indexOf(it) >= offset }
                    .take(count)
                    .sortedWith { p1, p2 ->
                        p1.date.compareTo(p2.date)
                    }
                    .also { // предупреждает, если какая-то из указанных заметок не найдена
                        noteIds.forEach {
                            if (!checkNoteId(it, notesList.toList())) println("Note with id $it not found!")
                        }
                    }
                    .let { return if (!sort) it.reversed() else it }
            }
        }
        return emptyList()
    }

    private fun checkNoteId(noteId: Long, notesList: List<Note>): Boolean {
        notesList.forEach {
            if (it.id == noteId) return true
        }
        return false
    }


    @JvmOverloads
    fun addNote(
        title: String,
        text: String,
        authorId: Long,
        privacy: Int = ALL_USERS,
        commentPrivacy: Int = ALL_USERS
    ): Long {
        if (privacy !in ALL_USERS..ONLY_YOU || commentPrivacy !in ALL_USERS..ONLY_YOU) {
            println("Error: Invalid privacy id! Must be in the range [$ALL_USERS; $ONLY_YOU]")
            return 0
        }
        val note = Note(
            id = noteId,
            title = title,
            text = text,
            authorId = authorId,
            privacy = privacy,
            commentPrivacy = commentPrivacy,
            date = LocalDateTime.now()
        )

        if (!notes.containsKey(authorId)) notes[authorId] = HashMap()
        notes[authorId]?.put(note.id, note)
        noteId++
        return note.id
    }

    @JvmOverloads
    fun createComment(
        noteId: Long,
        authorId: Long,
        ownerId: Long,
        message: String,
        replyToUserId: Long? = null,
        replyCommentId: Long? = null
    ): Long {
        val comment = Comment(
            id = guid,
            authorId = authorId,
            text = message,
            replyUserId = replyToUserId,
            replyCommentId = replyCommentId,
            date = LocalDateTime.now()
        )
        return if (notes.containsKey(ownerId) && notes[ownerId]?.containsKey(noteId) == true) {
            val note = getNoteById(noteId, ownerId)
            note.comments[comment.id] = comment
            guid++
            comment.id
        } else 0
    }

    fun deleteNote(noteId: Long, userId: Long): Boolean {
        if (notes.containsKey(userId) && notes[userId]?.containsKey(noteId) == true) {
            val note = getNoteById(noteId, userId)
            if (note.authorId == userId) {
                notes[userId]?.remove(noteId)
                return notes[userId]?.containsValue(note) == true
            }
        }
        return false
    }

    fun deleteComment(
        commentId: Long,
        noteId: Long,
        userId: Long,
        ownerId: Long,
    ): Boolean {
        if (notes.containsKey(ownerId) && notes[ownerId]?.containsKey(noteId) == true) {
            val note = getNoteById(noteId, ownerId)
            val comment = note.comments[commentId]
            if (userId == comment?.authorId) {
                note.comments.remove(commentId)
                note.deletedComments[commentId] = comment
                return !note.comments.containsValue(comment)
            }
        }
        return false
    }

    fun restoreComment(
        noteId: Long,
        ownerId: Long,
        commentId: Long,
    ): Boolean {
        if (notes.containsKey(ownerId) && notes[ownerId]?.containsKey(noteId) == true) {
            val note = getNoteById(noteId, ownerId)
            val comment = note.deletedComments[commentId]
            return if (comment != null) {
                note.comments[commentId] = comment
                note.deletedComments.remove(commentId)
                note.comments.containsValue(comment)
            } else {
                println("Error 404: comment not found")
                false
            }
        }
        return false
    }

    fun edit(
        noteId: Long,
        title: String,
        text: String,
        userId: Long,
        privacy: Int,
        commentPrivacy: Int,
    ): Boolean {
        if (privacy !in ALL_USERS..ONLY_YOU || commentPrivacy !in ALL_USERS..ONLY_YOU) {
            println("Error: Invalid privacy id! Must be in the range [$ALL_USERS; $ONLY_YOU]")
            return false
        }

        return if (notes.containsKey(userId) && notes[userId]?.containsKey(noteId) == true) {
            val note = getNoteById(noteId, userId)
            val newNote = note.copy(title = title, text = text, privacy = privacy, commentPrivacy = commentPrivacy)
            notes[userId]?.set(noteId, newNote)
            notes[userId]?.containsValue(newNote) == true
        } else false
    }

    fun editComment(
        commentId: Long,
        ownerId: Long,
        noteId: Long,
        userId: Long, //тот, кто пытается удалить
        message: String,
    ): Boolean {
        if (notes.containsKey(ownerId) && notes[ownerId]?.containsKey(noteId) == true) {
            val note = getNoteById(noteId, userId)
            val comment = note.comments[commentId]
            val newComment = comment?.copy(text = message)
            if (newComment != null) {
                note.comments[commentId] = newComment
                return note.comments.containsValue(newComment)
            }
        }
        return false
    }
}