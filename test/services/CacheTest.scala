package services

import java.sql.{Timestamp => SQLTimestamp}
import java.time.LocalDateTime

import org.scalatest._

class CacheTest extends FlatSpec with Matchers {

  def now() = SQLTimestamp.valueOf(LocalDateTime.now())

  "Cache" should "return the value inserted" in {
    val cache = new Cache[Int]

    cache.put("a", 1)
    cache.get("a") shouldBe Some(1)

  }

  it should "be empty after TTL has passed" in {
    val cache: Cache[Int] = new Cache[Int](15, Map("a" -> CacheItem(new SQLTimestamp(now().getTime - Int.MaxValue), 1)))
    cache.get("a") shouldBe None
  }

}
