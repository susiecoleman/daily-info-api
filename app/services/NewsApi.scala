package services

import java.sql.{Timestamp => SQLTimestamp}
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import cats.effect.Effect
import config.Config
import models.{Article, NewsApiResponse, Source}
import monix.eval.Task
import monix.eval.TaskCircuitBreaker.Timestamp
import org.http4s.Uri
import play.api.libs.ws.WSClient
import org.http4s.client.blaze._
// import org.http4s.client.blaze._

import org.http4s.client._
// import org.http4s.client._






class NewsApi[F[_]](val config: Config)(implicit F: Effect[F]) {

  val httpClientF: F[Client[F]] = Http1Client[F]()

  val cache = new Cache[Article]

  def getArticle(q: String): F[Article] = {
//    val request = ws.url("https://newsapi.org/v2/top-headlines")
//      .addQueryStringParameters(
//        "country" -> "gb",
//        "q" -> q,
//        "apiKey" -> config.newsApiKey)

    val url = Uri.uri("http://localhost:8080/hello/") +? ("country", "gb") +? ("q", q) +? ("apiKey", config.newsApiKey)

    httpClientF

    val r2 = for {
      client <- httpClientF
    } yield client

    F.delay(Article("a", Source("b")))

//    request.addHttpHeaders("Accept" -> "application/json")
//    cache.get(q).map(F.delay(_)).getOrElse(F.fromFuture(request.get.map(response => {
//      val article: Article = response.json.as[NewsApiResponse].articles.head
//      cache.put(q, article)
//      article
//    })))
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
