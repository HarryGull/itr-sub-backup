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

import config.{MicroserviceAppConfig, WSHttp}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import java.util.UUID

import fixtures.SubmissionFixture
import play.api.test.Helpers._
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.JsValue
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.http.ws.WSHttp
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

  "calling submitAA" should {
    "return a valid response" in new Setup {

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.anyString(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(HttpResponse(OK))

      val result = TestConnector.submitAA(validSubmissionJsVal, dummyTavcRef)
      await(result).status shouldBe OK
    }
  }

  "Calling submitAdvancedAssurance with a email with a valid model" should {
    "return a OK" in new Setup {

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(OK)))
      val result = TestConnector.submitAA(validSubmissionJsVal, dummyTavcRef)
      await(result).status shouldBe OK
    }
  }

  "Calling submitAdvancedAssurance with a email containing 'badrequest'" should {
    "return a BAD_REQUEST error" in new Setup{

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST)))
      val result = TestConnector.submitAA(validSubmissionJsVal, dummyTavcRef)
      await(result).status shouldBe BAD_REQUEST
    }
  }

  "Calling submitAdvancedAssurance with a email containing 'forbidden'" should {
    "return a FORBIDDEN Error" in new Setup {

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(FORBIDDEN)))
      val result = TestConnector.submitAA(validSubmissionJsVal, dummyTavcRef)
      await(result).status shouldBe FORBIDDEN
    }
  }

  "Calling submitAdvancedAssurance with a email containing 'serviceunavailable'" should {

    "return a SERVICE UNAVAILABLE ERROR" in new Setup  {

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(SERVICE_UNAVAILABLE)))
      val result = TestConnector.submitAA(validSubmissionJsVal, dummyTavcRef)
      await(result).status shouldBe SERVICE_UNAVAILABLE
    }
  }

  "Calling submitAdvancedAssurance with a email containing 'internalservererror'" should {

    "return a INTERNAL SERVER ERROR" in new Setup  {

      when(mockHttp.POST[JsValue, HttpResponse](Matchers.any(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR)))
      val result = TestConnector.submitAA(validSubmissionJsVal, dummyTavcRef)
      await(result).status shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
