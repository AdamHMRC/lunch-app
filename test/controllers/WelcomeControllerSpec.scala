package controllers

import com.google.inject.Guice
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.GreetingService

class WelcomeControllerSpec extends PlaySpec with GuiceOneAppPerTest {
  "WelcomeController GET" should {
    "return a successful reponse" in {
      val controller = Guice.createInjector().getInstance(classOf[WelcomeController])
      val result = controller.welcome(FakeRequest())
      status(result) mustBe OK
      contentType(result) mustBe Some("text/html")
    }

    "repond to the /welcome url" in {
      val request = FakeRequest(GET, "/welcome").withHeaders("Host" -> "localhost")
      val home = route(app, request).get
      status(home) mustBe OK
    }

    "say good morning and have a title" in {
      val controller = new WelcomeController(new FakeGreeterWithGreetingMessage("Good morning!"))
      val result = controller.welcome(FakeRequest())
      contentAsString(result) must include ("<h1>Good morning!</h1>")
      contentAsString(result) must include ("<title>Welcome!</title>")
    }

    "say good afternoon when it's the afternoon and have a title" in {
      val controller = new WelcomeController(new FakeGreeterWithGreetingMessage("Good afternoon!"))
      val result = controller.welcome(FakeRequest())
      contentAsString(result) must not include ("<h1>Good morning!</h1>")
      contentAsString(result) must include ("<h1>Good afternoon!</h1>")
      contentAsString(result) must include ("<title>Welcome!</title>")
    }
  }

  class FakeGreeterWithGreetingMessage(greetingMessage: String) extends GreetingService {
    override def greeting: String = {
      greetingMessage
    }
  }

}
