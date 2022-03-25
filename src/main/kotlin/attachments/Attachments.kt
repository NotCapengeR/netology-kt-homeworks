package attachments

abstract class Attachments(open val type: String)

data class VideoAttachment(
    override val type: String,
    val video: Video
) : Attachments(type)

data class PhotoAttachment(
    override val type: String,
    val photo: Photo
) : Attachments(type)

data class AudioAttachment(
    override val type: String,
    val audio: Audio
) : Attachments(type)

data class DocumentAttachment(
    override val type: String,
    val document: Document
) : Attachments(type)

data class MapAttachment(
    override val type: String,
    val id: Long,
    val ownerId: Long,
    val userId: Long,
) : Attachments(type)

data class Map(
    val id: Long,
    val name: String,
    val ownerId: Long,
    val userId: Long,
    val geo: String,
    val type: String = "map"
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
    val type: String = "audio"
)

data class Video(
    val id: Long,
    val name: String,
    val fileFormat: String,
    val ownerId: Long,
    val userId: Long,
    val author: String,
    val quality: Int,
    val type: String = "video"
)

data class Photo(
    val id: Long,
    val name: String,
    val ownerId: Long,
    val userId: Long,
    val fileFormat: String,
    val resolution: Int,
    val author: String,
    val type: String = "photo"
)