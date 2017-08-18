package controllers

import models.Sandwich
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import services.SandwichService

import scala.concurrent.Future

//default execution context https://www.playframework.com/documentation/2.5.x/ScalaAsync
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class SandwichControllerSpec extends PlaySpec with GuiceOneAppPerTest {
  "SandwichController" should {
    "inform the user we're sold out when there are no sandwiches" in {
      val application = new GuiceApplicationBuilder().
        overrides(bind[SandwichService].to[FakeSandwichService]).
        build

      // Need to specify Host header to get through AllowedHostsFilter
      val request = FakeRequest(GET, "/sandwiches").withHeaders("Host" -> "localhost")
      val home = route(application, request).get

      //sanitation
      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("<title>Sandwiches</title>")
      contentAsString(home) must include("<h1>Have a look at today's sandwiches</h1>")

      //sandwich behaviour
      contentAsString(home) must include("<p>Sorry, we're sold out</p>")
    }
  }


  "give a helpful message when sold out" in {
    val controller = new SandwichController(new FakeSandwichService(List()))
    val result = controller.sandwiches().apply(FakeRequest())
    contentAsString(result) must include("<p>Sorry, we're sold out</p>")
  }

  "show a single sandwich when only one is available" in {
    val controller = new SandwichController(new FakeSandwichService(List(Sandwich("Ham", 1.55, "Very tasty"))))
    val result = controller.sandwiches().apply(FakeRequest())

    contentAsString(result) must not include("<p>Sorry, we're sold out</p>")
    contentAsString(result) must include ("Please choose a sandwich")
    contentAsString(result) must include ("Ham")
    contentAsString(result) must include ("Very tasty")
    contentAsString(result) must include ("£1.55")
  }

  "show multiple sandwiches when more than one is available" in {
    val controller = new SandwichController(new FakeSandwichService(List(
      Sandwich("Ham", 1.55, "Very tasty"),
      Sandwich("Cheese", 2.55, "Cheese tastic"),
      Sandwich("Egg", 1.15, "Fresh")
    )))
    val result = controller.sandwiches().apply(FakeRequest())

    contentAsString(result) must not include("<p>Sorry, we're sold out</p>")
    contentAsString(result) must include ("Ham")
    contentAsString(result) must include ("Very tasty")
    contentAsString(result) must include ("£1.55")
    contentAsString(result) must include ("Cheese")
    contentAsString(result) must include ("Cheese tastic")
    contentAsString(result) must include ("£2.55")
    contentAsString(result) must include ("Egg")
    contentAsString(result) must include ("Fresh")
    contentAsString(result) must include ("£1.15")
  }
}

class FakeSandwichService(sandwichList: List[Sandwich]) extends SandwichService {

  def this() {
    this(List())
  }

  override def sandwiches: Future[List[Sandwich]] = {
    Future(sandwichList)
  }
}
