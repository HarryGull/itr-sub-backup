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

import java.util.UUID

import config.WSHttp
import fixtures.SubmissionFixture
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{BAD_REQUEST, _}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmissionDESConnectorSpec extends UnitSpec with MockitoSugar with OneAppPerSuite with SubmissionFixture {

  val mockHttp : WSHttp = mock[WSHttp]
  val sessionId = UUID.randomUUID.toString

  class Setup {
    object TestConnector extends SubmissionDESConnector {
      override val serviceUrl = "dummy"
      override val http = mockHttp
      override val environment = "test"
      override val token = "token"
    }
  }

  implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders("Accept" -> "application/vnd.hmrc.1.0+json", "Content-Type" -> "application/json")

  "AuthConnector" should {
    "use the correct http object" in {
      SubmissionDESConnector.http shouldBe WSHttp
    }
  }

  "calling submit" should {
    "return a valid response for an AA request" in new Setup {

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.anyString(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(HttpResponse(OK))

      val result = TestConnector.submit(validSubmissionJsValAA, dummyTavcRef)
      await(result).status shouldBe OK
    }

    "return a valid response for a CS request" in new Setup {

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.anyString(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(HttpResponse(SERVICE_UNAVAILABLE))

      val result = TestConnector.submit(validSubmissionJsValCS, dummyTavcRef)
      await(result).status shouldBe SERVICE_UNAVAILABLE
    }
  }

  "Calling getAASubmissionDetails with a TAVC account authorized" should {

    "return a valid response" in new Setup {
      when(mockHttp.GET[HttpResponse](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(OK, Some(validSubmissionDetailsJsVal))))
      val result = TestConnector.getAASubmissionDetails(dummyTavcRef)
      await(result) match {
        case response => {
          response.status shouldBe OK
        }
      }
    }
  }

  "SubmissionController.getAASubmissionDetails with a TAVC account not authorized" should {

    "return a response" in new Setup {
      when(mockHttp.GET[HttpResponse](Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, None)))
      val result = TestConnector.getAASubmissionDetails(dummyTavcRef)
      await(result) match {
        case response => {
          response.status shouldBe BAD_REQUEST
        }
      }
    }
  }
}
