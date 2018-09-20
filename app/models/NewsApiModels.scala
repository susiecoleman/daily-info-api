package models

import play.api.libs.json.{Json, Reads}

case class Source(name: String)
object Source{
  implicit val sourceReads: Reads[Source] = Json.reads[Source]
}
case class Article(title: String, source: Source)
object Article {
  implicit val articleReads: Reads[Article] = Json.reads[Article]
}
case class NewsApiResponse(articles: List[Article])
object NewsApiResponse {
  implicit val newsApiResponseReads: Reads[NewsApiResponse] = Json.reads[NewsApiResponse]
}
