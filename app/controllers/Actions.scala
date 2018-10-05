package controllers
import monix.eval.Task
import play.api.mvc._
import monix.execution.Scheduler.Implicits.global

object Actions {

  def taskActionAsync[F] (task: Task[Result]): Action[AnyContent] = {
    Action.async(task.runAsync)
  }


}

