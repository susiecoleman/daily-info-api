package services

import config.Config
import models.{Article, NewsApiResponse}
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NewsApi(val ws: WSClient, val config: Config) {

  def getArticle: Future[Option[Article]] = {
    val request = ws.url("https://newsapi.org/v2/top-headlines")
      .addQueryStringParameters(
        "country" -> "gb",
        "apiKey" -> config.newsApiKey)
      .addHttpHeaders("Accept" -> "application/json")
    request.get.map(response => {
      response.json.as[NewsApiResponse].articles.headOption
    })
  }

}
