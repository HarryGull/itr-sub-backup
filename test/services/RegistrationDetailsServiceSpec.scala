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

package services

import connectors.RegistrationDetailsConnector
import org.mockito.Matchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.test.Helpers._
import play.api.libs.json.Json
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse, Upstream5xxResponse}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class RegistrationDetailsServiceSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val mockRegistrationDetailsConnector = mock[RegistrationDetailsConnector]
  val safeID = "XA0001234567890"
  val responseJson = Json.parse("""{"test":"json"}""")
  implicit val hc = HeaderCarrier()

  object TestService extends RegistrationDetailsService {
    override lazy val registrationDetailsConnector = mockRegistrationDetailsConnector
  }

  "RegistrationDetailsService" should {
    "Use the correct RegistrationDetailsConnector" in {
      RegistrationDetailsService.registrationDetailsConnector shouldBe RegistrationDetailsConnector
    }
  }

  "getRegistrationDetails" when {

    "A successful HTTP Response is returned from the connector" should {

      lazy val result = TestService.getRegistrationDetails(safeID)

      "return the response code from the connector" in {
        when(mockRegistrationDetailsConnector.getRegistrationDetails(Matchers.eq(safeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(HttpResponse(OK, Some(responseJson))))
        status(result) shouldBe OK
      }

      "return the successful response from the connector" in {
        when(mockRegistrationDetailsConnector.getRegistrationDetails(Matchers.eq(safeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(HttpResponse(OK, Some(responseJson))))
        Json.parse(contentAsString(result)) shouldBe responseJson
      }
    }

    "A failed response is returned from the connector" should {

      lazy val result = TestService.getRegistrationDetails(safeID)

      "return INTERNAL_SERVER_ERROR" in {
        when(mockRegistrationDetailsConnector.getRegistrationDetails(Matchers.eq(safeID))(Matchers.any(), Matchers.any()))
          .thenReturn(Future.failed(Upstream5xxResponse("Internal server error",INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR)))
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

    }
  }

}
