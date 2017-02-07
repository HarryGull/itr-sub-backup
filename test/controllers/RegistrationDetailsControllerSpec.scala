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

package controllers

import connectors.AuthConnector
import helpers.AuthHelper._
import org.mockito.Matchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json.Json
import services.RegistrationDetailsService
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class RegistrationDetailsControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  val mockRegistrationDetailsService = mock[RegistrationDetailsService]
  val safeID = "XA0001234567890"
  val responseJson = Json.parse("""{"test":"json"}""")

  object TestController extends RegistrationDetailsController {
    override val authConnector: AuthConnector = mockAuthConnector
    override val registrationDetailsService = mockRegistrationDetailsService
  }

  "RegistrationDetailsController" should {
    "Use the correct RegistrationDetailsService" in {
      RegistrationDetailsController.registrationDetailsService shouldBe RegistrationDetailsService
    }
    "Use the correct AuthConnector" in {
      RegistrationDetailsController.authConnector shouldBe AuthConnector
    }
  }

  "getRegistrationDetails" when {

    "calling the method with a TAVC account with status Activated and confidence level 50" should {

      lazy val result = TestController.getRegistrationDetails(safeID)(FakeRequest())

      "return the response code from registration details service" in {
        setup()
        when(mockRegistrationDetailsService.getRegistrationDetails(Matchers.eq(safeID))(Matchers.any(),Matchers.any()))
          .thenReturn(Future.successful(Ok(responseJson)))
        status(result) shouldBe OK

      }

      "return the response json from registration details service" in {
        setup()
        when(mockRegistrationDetailsService.getRegistrationDetails(Matchers.eq(safeID))(Matchers.any(),Matchers.any()))
          .thenReturn(Future.successful(Ok(responseJson)))
        Json.parse(contentAsString(result)) shouldBe responseJson

      }
    }

    "calling the method with a TAVC account with status NotYetActivated and confidence level 50" should {
      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.getRegistrationDetails(safeID)(FakeRequest())
        status(result) shouldBe FORBIDDEN
      }
    }
  }

}
