package services

import com.google.inject.ImplementedBy
import models.Sandwich
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.ws.WSGet

import scala.concurrent.Future

//default execution context https://www.playframework.com/documentation/2.5.x/ScalaAsync
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class SandwichLady extends SandwichService {
  val SANDWICH_SERVICE_URL = "http://localhost:3000/sandwiches"

  override def sandwiches: Future[List[Sandwich]] = {
    val http = new WSGet {
      override val hooks = NoneRequired
    }

    implicit val hc = HeaderCarrier()
    http.GET[List[Sandwich]](SANDWICH_SERVICE_URL)
  }
}

@ImplementedBy(classOf[SandwichLady])
trait SandwichService {
  def sandwiches: Future[List[Sandwich]]
}
