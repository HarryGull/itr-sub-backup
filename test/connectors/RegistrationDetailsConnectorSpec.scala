/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import config.WSHttp
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class RegistrationDetailsConnectorSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val mockHttp = mock[WSHttp]
  val safeID = "XA0001234567890"
  val responseJson = Json.parse("""{"test":"json"}""")
  implicit val hc = HeaderCarrier()

  object TestConnector extends RegistrationDetailsConnector {
    override lazy val http = mockHttp
    override lazy val safeIDQuery: String = "safeid="
    override lazy val getRegistrationDetailsURL: String = "/get-registration-details"
    override lazy val environment: String = "test"
    override lazy val serviceUrl: String = "test.service"
    override lazy val token = "token"
  }

  "RegistrationDetailsConnector" should {
    "Use WsHttp" in {
      RegistrationDetailsConnector.http shouldBe WSHttp
    }
  }

  "getRegistrationDetails" should {

    lazy val result = TestConnector.getRegistrationDetails(safeID)

    "return the http GET response code" in {
      when(mockHttp.GET[HttpResponse](Matchers.eq(
        s"${TestConnector.serviceUrl}${TestConnector.getRegistrationDetailsURL}?${TestConnector.safeIDQuery}$safeID"))
      (Matchers.any(),Matchers.any())).thenReturn(Future.successful(HttpResponse(OK,Some(responseJson))))
      await(result).status shouldBe OK
    }

    "return the http GET response json" in {
      when(mockHttp.GET[HttpResponse](Matchers.eq(
        s"${TestConnector.serviceUrl}${TestConnector.getRegistrationDetailsURL}?${TestConnector.safeIDQuery}$safeID"))
        (Matchers.any(),Matchers.any())).thenReturn(Future.successful(HttpResponse(OK,Some(responseJson))))
      await(result).json shouldBe responseJson
    }

  }

}
