package services

import config.Config
import models.{Article, NewsApiResponse}
import monix.eval.Task
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global

class NewsApi(val ws: WSClient, val config: Config) {

  var cache = Map.empty[String, Article]

  def getArticle(q: String): Task[Article] = {
    val request = ws.url("https://newsapi.org/v2/top-headlines")
      .addQueryStringParameters(
        "country" -> "gb",
        "q" -> q,
        "apiKey" -> config.newsApiKey)

    request.addHttpHeaders("Accept" -> "application/json")
    cache.get(q).map(Task(_)).getOrElse(Task.fromFuture(request.get.map(response => {
      val article: Article = response.json.as[NewsApiResponse].articles.head
      cache = cache + (q -> article)
      article
    })))
  }
}
