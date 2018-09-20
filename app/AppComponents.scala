import config.Config
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.api.libs.ws.ahc.AhcWSComponents
import router.Routes
import services.NewsApi

class AppComponents(context: Context) extends BuiltInComponentsFromContext(context) with HttpFiltersComponents with AhcWSComponents {

  lazy val router: Router = new Routes(httpErrorHandler, appController)

  lazy val config = new Config(configuration)
  lazy val newsApi = new NewsApi(wsClient, config)
  lazy val appController = new controllers.App(controllerComponents, newsApi)
}
