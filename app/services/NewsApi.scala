package services

import config.Config
import models.{Article, NewsApiResponse}
import monix.eval.Task
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global

class NewsApi(val ws: WSClient, val config: Config) {

  val cache = Map.empty[String, Article]

  def getArticle: Task[Article] = {
    val request = ws.url("https://newsapi.org/v2/top-headlines")
      .addQueryStringParameters(
        "country" -> "gb",
        "apiKey" -> config.newsApiKey)

    val url = request.url
      request.addHttpHeaders("Accept" -> "application/json")
    cache.get(url).map(Task(_)).getOrElse(Task.fromFuture(request.get.map(response => {
      response.json.as[NewsApiResponse].articles.head
    })))
  }
}
