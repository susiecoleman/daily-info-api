package services

import java.sql.{Timestamp => SQLTimestamp}
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import config.Config
import io.circe.generic.auto._
import models.{Article, NewsApiResponse}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import monix.execution.Scheduler.{global => scheduler}
import org.http4s.Uri
import org.http4s.circe._
import org.http4s.client.blaze._

class NewsApi(val config: Config) {

  val cache = new Cache[Article]

  def getArticle(q: String): Task[Article] = {
    val url = Uri.uri("https://newsapi.org/v2/top-headlines") +?
      ("country", "gb") +?
      ("q", q) +?
      ("apiKey", config.newsApiKey)

    for {
      client <- Http1Client[Task]()
      apiResponse <- client.expect(url)(jsonOf[Task, NewsApiResponse])
    } yield apiResponse.articles.head
  }

}


case class CacheItem[A](timestamp: SQLTimestamp, item: A)

class Cache[A](val ttlInSeconds: Int = 15, val initialCache: Map[String, CacheItem[A]] = Map.empty[String, CacheItem[A]]) {

  private def now() = SQLTimestamp.valueOf(LocalDateTime.now())
  private var cache = initialCache

  def get(key: String): Option[A] = {
    cache.get(key).filter(isFresh).map(_.item)

  }

  private def isFresh(item: CacheItem[A]): Boolean = {
    val limit = new SQLTimestamp(now().getTime - ttlInSeconds * 1000)
    item.timestamp.after(limit)
  }

  def put(key: String, article: A) = {
    cache = cache + (key -> CacheItem(now() , article))
  }

  private val c = scheduler.scheduleWithFixedDelay(
    10, 10, TimeUnit.SECONDS,
    new Runnable {
      def run(): Unit = {
        cache = cache.filter{ case (_, item) => isFresh(item) }
      }
    })

}
