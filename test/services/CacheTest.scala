package services

import org.scalatest._

class CacheTest extends FlatSpec with Matchers {

  def testCache(items: Seq[CacheItem[Int]]) = new TestCache[Int](ttlInSeconds = 15) {
    private var cache  = Map.empty
  }

  "Cache" should "return the value inserted" in {
    val cache = new Cache[Int]

    cache.put("a", 1)
    cache.get("a") shouldBe Some(1)

  }

  it should "be empty after TTL has passed" in {
    val cache = new Cache[Int](1)

    cache.put("a", 1)
    Thread.sleep(3001)
    cache.get("a") shouldBe None
  }

}
