package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import services.GreetingService

class WelcomeController @Inject()(greeter: GreetingService) extends Controller {
  def welcome = Action {
    Ok(views.html.welcome(greeter.greeting))
  }
}
