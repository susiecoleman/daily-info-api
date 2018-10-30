package services

import java.sql.{Timestamp => SQLTimestamp}
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import config.Config
import models.{Article, NewsApiResponse}
import monix.eval.Task
import play.api.libs.ws._

import org.http4s.Uri
//import cats.effect.Effect
//import org.http4s.client.blaze._
//import org.http4s.client._
//import cats._
//import cats.effect._
//import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import monix.execution.Scheduler.{global => scheduler}

class NewsApi(val config: Config, val ws: WSClient) {

//  // I don't see a way to do this without implementing Effect[Task]
//  implicit val taskEffect: Effect[Task] = ???
//
//    //From the docs. Uses IO from the cats library which is equivalent to Task
//  val httpClientF = Http1Client[IO]()
//
//    //In the case of Task
//    val httpClientF2 = Http1Client[Task]()

  val cache = new Cache[Article]

  def getArticle(q: String): Task[Article] = {
    val request = ws.url("https://newsapi.org/v2/top-headlines")
      .addQueryStringParameters(
        "country" -> "gb",
        "q" -> q,
        "apiKey" -> config.newsApiKey)

    val url = Uri.uri("http://localhost:8080/hello/") +? ("country", "gb") +? ("q", q) +? ("apiKey", config.newsApiKey)

    request.addHttpHeaders("Accept" -> "application/json")
    cache.get(q).map(Task(_)).getOrElse(Task.fromFuture(request.get.map(response => {
      val article: Article = response.json.as[NewsApiResponse].articles.head
      cache.put(q, article)
      article
    })))
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
