package services

import java.sql.{Timestamp => SQLTimestamp}
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

import config.Config
import models.{Article, NewsApiResponse}
import monix.eval.Task
import monix.eval.TaskCircuitBreaker.Timestamp
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import monix.execution.Scheduler.{global => scheduler}

class NewsApi(val ws: WSClient, val config: Config) {

  val cache = new Cache[Article]

  def getArticle(q: String): Task[Article] = {
    val request = ws.url("https://newsapi.org/v2/top-headlines")
      .addQueryStringParameters(
        "country" -> "gb",
        "q" -> q,
        "apiKey" -> config.newsApiKey)

    request.addHttpHeaders("Accept" -> "application/json")
    cache.get(q).map(Task(_)).getOrElse(Task.fromFuture(request.get.map(response => {
      println("Hitting api")
      val article: Article = response.json.as[NewsApiResponse].articles.head
      cache.put(q, article)
      article
    })))
  }
}


case class CacheItem[A](timestamp: SQLTimestamp, item: A)

class Cache[A](val ttlInSeconds: Int = 15, now: => SQLTimestamp = SQLTimestamp.valueOf(LocalDateTime.now())) {



  private var cache = Map.empty[String, CacheItem[A]]

  def get(key: String): Option[A] = {
    println(cache)
    cache.get(key).map(_.item)
  }

  def put(key: String, article: A, putNow: => SQLTimestamp = now) = {

    cache = cache + (key -> CacheItem(putNow , article))
  }


  private val c = scheduler.scheduleWithFixedDelay(
    1, 1, TimeUnit.SECONDS,
    new Runnable {
      def run(): Unit = {
        val limit = new SQLTimestamp(now.getTime - ttlInSeconds * 1000)
        cache = cache.filter{ case (_, item) => item.timestamp.after(limit) }
      }
    })

}
