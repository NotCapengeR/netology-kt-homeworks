package wall

import attachments.Attachments

import comments.Comment
import notes.Note
import notes.NoteNotFoundException
import notes.UserNotFoundException
import reports.Report
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

// access levels
private const val ALL_USERS = 1
private const val FRIENDS_ONLY = 2
private const val FRIENDS_AND_THEIR_FRIENDS = 3
private const val ONLY_YOU = 4

fun main() {
    val valera = User(1, "Valera")
    val kirkorov = User(2, "Филипп Киркоров")
    NoteService.addNote("ewq0", "241jkl124", kirkorov.id)
    NoteService.addNote("ew4", "241jkl132124", kirkorov.id)
    NoteService.addNote("ewq3", "241jkl131224", kirkorov.id)
    NoteService.addNote("ewq2", "241jkl123124", kirkorov.id)
    NoteService.addNote("ewq1", "12241jkl124", kirkorov.id)
    println(NoteService.getNotes(kirkorov.id, false, 0, 20, 5, 1, 10))
    println(NoteService.getNotes(kirkorov.id, false, 0, 20, 2))
    NoteService.createComment(2, valera.id, kirkorov.id, "test1")
    NoteService.createComment(2, valera.id, kirkorov.id, "test2")
    NoteService.createComment(2, valera.id, kirkorov.id, "test3")
    NoteService.createComment(2, valera.id, kirkorov.id, "test4")
    NoteService.createComment(2, valera.id, kirkorov.id, "test5")
    println(NoteService.getComments(2, kirkorov.id, true))
}


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
            commentPrivacy = commentPrivacy
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
            replyCommentId = replyCommentId
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


object WallService {
    private val posts = HashMap<Long, HashMap<Long, Post>>()
    private var id = 1L
    private var commentId = 1L
    private var reportId = 1L
    private val reasons = makeReasons()

    private fun makeReasons(): HashMap<Int, String> {
        val reasons = HashMap<Int, String>()
        reasons[0] = "Спам"
        reasons[1] = "Детская порнография"
        reasons[2] = "Экстремизм"
        reasons[3] = "Пропаганда наркотиков"
        reasons[4] = "Насилие"
        reasons[5] = "Материал для взрослых"
        reasons[6] = "Оскорбление"
        reasons[8] = "Призыв к суициду"
        return reasons
    }

    fun outputUserWall(user: User) {
        posts[user.id]?.values?.forEach {
            println(it) // Лень было toString() переопределять для красивого вывода)
        }               // Энивей для нормального отображения будет UI использоваться
    }

    fun outputAttachments(postId: Long) {
        when (val result = findPostById(postId)) {
            is PostSearchResult.Success -> println("Attachments: ${result.post.attachments}")
            is PostSearchResult.PostNotFound -> println("Post not found!")
        }
    }

    fun report(commentId: Long, postId: Long, reason: Int): Boolean {
        when (val result = findPostById(postId)) {
            is PostSearchResult.Success -> {
                if (reasons.containsKey(reason)) {
                    val post = result.post
                    val report = reasons[reason]?.let { Report(id = reportId, commentId = commentId, reason = it) }
                    if (post.comments.containsKey(commentId) && report != null) {
                        post.comments[commentId]?.reports?.put(report.id, report)
                        return post.comments[commentId]?.reports?.containsValue(report) == true
                    } else throw CommentNotFoundException()
                } else throw InvalidReasonException()
            }
            is PostSearchResult.PostNotFound -> throw PostNotFoundException()
        }
    }

    @JvmOverloads
    fun createComment(
        text: String,
        authorId: Long,
        postId: Long,
        replyUserId: Long? = null,
        replyCommentId: Long? = null
    ) {
        when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                val comment = Comment(
                    id = commentId,
                    authorId = authorId,
                    text = text,
                    replyUserId = replyUserId,
                    replyCommentId = replyCommentId
                )
                post.comments[commentId] = comment
                commentId++
            }
            is PostSearchResult.PostNotFound -> throw PostNotFoundException()
        }
    }

    @JvmOverloads
    fun addPost(
        postText: String,
        wallOwner: User,
        author: User,
        copyright: String? = null,
        replyPost: Post? = null,
        isPinned: Boolean = false,
        friendOnly: Boolean = false,
        markedAsAd: Boolean = false,
    ): Boolean { //Always true
        val post = Post(
            id = id,
            wallOwnerId = wallOwner.id,
            authorId = author.id,
            authorName = author.name,
            text = postText,
            copyright = copyright,
            replyOwnerId = replyPost?.authorId,
            replyPostId = replyPost?.id,
            isPinned = isPinned,
            friendOnly = friendOnly,
            markedAsAd = markedAsAd,
        )

        if (!posts.containsKey(wallOwner.id)) {
            posts[wallOwner.id] = HashMap()
        }
        posts[wallOwner.id]?.put(post.id, post)
        id++
        return posts[wallOwner.id]?.containsValue(post) == true
    }

    fun updatePost(postId: Long, newText: String, updateAuthor: User): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                if (updateAuthor.id == post.authorId) {
                    val newPost = post.copy(text = newText)
                    newPost.editHistory.add(post.text)
                    val postsList = posts[post.wallOwnerId]
                    postsList?.set(postId, newPost)
                    postsList?.containsValue(newPost) == false
                } else false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    fun deletePost(postId: Long): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                val postsList = posts[post.wallOwnerId]
                postsList?.remove(post.id)
                postsList?.containsValue(post) == false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    fun attach(postId: Long, attachAuthor: User, attachment: Attachments): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                if (attachAuthor.id == post.authorId) {
                    post.attachments.add(attachment)
                    return post.attachments.contains(attachment)
                } else false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    private fun findPostById(postId: Long): PostSearchResult {
        posts.values.forEach {
            if (it.containsKey(postId) && it[postId] != null) return PostSearchResult.Success(it[postId]!!)
        }
        return PostSearchResult.PostNotFound
    }
}

class WallServiceForTests {
    private val posts = HashMap<Long, HashMap<Long, Post>>()
    private var id = 1L
    private var commentId = 1L
    private var reportId = 1L
    private val reasons = makeReasons()

    private fun makeReasons(): HashMap<Int, String> {
        val reasons = HashMap<Int, String>()
        reasons[0] = "Спам"
        reasons[1] = "Детская порнография"
        reasons[2] = "Экстремизм"
        reasons[3] = "Пропаганда наркотиков"
        reasons[4] = "Насилие"
        reasons[5] = "Материал для взрослых"
        reasons[6] = "Оскорбление"
        reasons[8] = "Призыв к суициду"
        return reasons
    }

    fun report(commentId: Long, postId: Long, reason: Int): Boolean {
        when (val result = findPostById(postId)) {
            is PostSearchResult.Success -> {
                if (reasons.containsKey(reason)) {
                    val post = result.post
                    val report = reasons[reason]?.let { Report(id = reportId, commentId = commentId, reason = it) }
                    if (post.comments.containsKey(commentId) && report != null) {
                        post.comments[commentId]?.reports?.put(report.id, report)
                        return post.comments[commentId]?.reports?.containsValue(report) == true
                    } else throw CommentNotFoundException()
                } else throw InvalidReasonException()
            }
            is PostSearchResult.PostNotFound -> throw PostNotFoundException()
        }
    }

    @JvmOverloads
    fun createComment(
        text: String,
        authorId: Long,
        postId: Long,
        replyUserId: Long? = null,
        replyCommentId: Long? = null
    ) {
        when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                val comment = Comment(
                    id = commentId,
                    authorId = authorId,
                    text = text,
                    replyUserId = replyUserId,
                    replyCommentId = replyCommentId
                )
                post.comments[commentId] = comment
                commentId++
            }
            is PostSearchResult.PostNotFound -> throw PostNotFoundException()
        }
    }

    @JvmOverloads
    fun addPost(
        postText: String,
        wallOwner: User,
        author: User,
        copyright: String? = null,
        replyPost: Post? = null,
        isPinned: Boolean = false,
        friendOnly: Boolean = false,
        markedAsAd: Boolean = false,
    ): Boolean { //Always true
        val post = Post(
            id = id,
            wallOwnerId = wallOwner.id,
            authorId = author.id,
            authorName = author.name,
            text = postText,
            copyright = copyright,
            replyOwnerId = replyPost?.authorId,
            replyPostId = replyPost?.id,
            isPinned = isPinned,
            friendOnly = friendOnly,
            markedAsAd = markedAsAd,
        )

        if (!posts.containsKey(wallOwner.id)) {
            posts[wallOwner.id] = HashMap()
        }
        posts[wallOwner.id]?.put(post.id, post)
        id++
        return posts[wallOwner.id]?.containsValue(post) == true
    }

    fun updatePost(postId: Long, newText: String, updateAuthor: User): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                if (updateAuthor.id == post.authorId) {
                    val newPost = post.copy(text = newText)
                    newPost.editHistory.add(post.text)
                    val postsList = posts[post.wallOwnerId]
                    postsList?.set(postId, newPost)
                    postsList?.containsValue(newPost) == true
                } else false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    fun deletePost(postId: Long): Boolean {
        return when (val result: PostSearchResult = findPostById(postId)) {
            is PostSearchResult.Success -> {
                val post = result.post
                val postsList = posts[post.wallOwnerId]
                postsList?.remove(post.id)
                postsList?.containsValue(post) == false
            }
            is PostSearchResult.PostNotFound -> false
        }
    }

    fun findPostById(postId: Long): PostSearchResult {
        posts.values.forEach {
            if (it.containsKey(postId) && it[postId] != null) return PostSearchResult.Success(it[postId]!!)
        }
        return PostSearchResult.PostNotFound
    }
}