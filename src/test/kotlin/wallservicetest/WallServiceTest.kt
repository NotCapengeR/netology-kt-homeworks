package wallservicetest

import junit.framework.TestCase.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import wall.*

class WallServiceTest {

    @Test
    fun `comment not found report`() {
        assertThrows<CommentNotFoundException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
            service.report(124, 1, 0)
        }
    }

    @Test
    fun `post not found report`() {
        assertThrows<PostNotFoundException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
            service.report(1, 3121, 0)
        }
    }

    @Test
    fun `invalid reason report`() {
        assertThrows<InvalidReasonException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
            service.report(1, 1, 132132)
        }
    }

    @Test
    fun `should throw`() {
        assertThrows<PostNotFoundException> {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 121)
        }
    }

    @Test
    fun `shouldn't throw`() {
        assertDoesNotThrow {
            val service = WallServiceForTests()
            val owner = User(1, "zzzzz")
            service.addPost("afskhjfjasfafhsja", owner, owner)
            service.createComment("1k2hh3jk2", 1, 1)
        }
    }

    @Test
    fun `add check id`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("afskhjfjasfafhsja", owner, owner)
        val post = service.findPostById(0).post
        assertNull(post)
    }

    @Test
    fun `update existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.updatePost(2, "1lk23j13ljk2", owner)
        assertTrue(result)
    }

    @Test
    fun `update not existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.updatePost(10, "1lk23j13ljk2", owner)
        assertFalse(result)
    }

    @Test
    fun `delete existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.deletePost(2)
        assertTrue(result)
    }

    @Test
    fun `delete not existing`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("fashjkafskhjfshj", owner, owner)
        service.addPost("31223312fashjkafskhjfshj", owner, owner)
        service.addPost("fashjkafskh31212234jfshj", owner, owner)
        val result = service.deletePost(10)
        assertFalse(result)
    }
}