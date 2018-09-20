package config

import play.api.{Configuration => PlayConfiguration}

class Config(val playConfiguration: PlayConfiguration) {

  val newsApiKey: String = playConfiguration.get[String]("news-api-key")

}
