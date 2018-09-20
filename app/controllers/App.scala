package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.NewsApi

import scala.concurrent.ExecutionContext.Implicits.global

class App (val controllerComponents: ControllerComponents, val newsAPI: NewsApi) extends BaseController  {

  def index: Action[AnyContent] = Action.async(
    newsAPI.getArticle.map {
      case Some(a) => Ok(s"Headline: ${a.title}\nSource: ${a.source.name}")
      case _ => BadRequest
    }
  )

}