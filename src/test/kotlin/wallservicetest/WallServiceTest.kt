package wallservicetest

import junit.framework.TestCase.*
import org.junit.jupiter.api.Test
import wall.User
import wall.WallServiceForTests

class WallServiceTest {

    @Test
    fun `add check id`() {
        val service = WallServiceForTests()
        val owner = User(1, "zzzzz")
        service.addPost("afskhjfjasfafhsja", owner, owner)
        val post = service.findPostById(0)
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