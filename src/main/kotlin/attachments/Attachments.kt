package attachments

sealed class Attachments(open val type: String)

data class VideoAttachment(
    override val type: String = "video",
    val video: Video,
) : Attachments(type)

data class PhotoAttachment(
    override val type: String = "photo",
    val photo: Photo,
) : Attachments(type)

data class AudioAttachment(
    override val type: String = "audio",
    val audio: Audio,
) : Attachments(type)

data class DocumentAttachment(
    override val type: String = "document",
    val document: Document,
) : Attachments(type)

data class MapAttachment(
    override val type: String = "map",
    val map: Map,
) : Attachments(type)

data class Map(
    val id: Long,
    val name: String,
    val ownerId: Long,
    val userId: Long,
    val geo: String,
)

data class Document(
    val id: Long,
    val name: String,
    val ownerId: Long,
    val userId: Long,
    val fileFormat: String,
)

data class Audio(
    val id: Long,
    val name: String,
    val ownerId: Long,
    val userId: Long,
    val fileFormat: String,
    val singer: String,
    val quality: Int,
)

data class Video(
    val id: Long,
    val name: String,
    val fileFormat: String,
    val ownerId: Long,
    val userId: Long,
    val author: String,
    val quality: Int,
)

data class Photo(
    val id: Long,
    val name: String,
    val ownerId: Long,
    val userId: Long,
    val fileFormat: String,
    val resolution: Int,
    val author: String,
)