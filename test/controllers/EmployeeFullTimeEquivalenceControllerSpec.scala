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
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.test.FakeRequest
import org.mockito.Mockito._
import services.EmployeeFullTimeEquivalenceService
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._
import org.scalatest.mock.MockitoSugar

class EmployeeFullTimeEquivalenceControllerSpec extends UnitSpec with MockitoSugar with OneAppPerSuite with BeforeAndAfter{

  val negativeNumberMessage = "Negative Number"
  val invalidNumberMessage = "Invalid Number"

  val successResponse: Boolean => Result = bool => Status(OK)(Json.toJson(bool))
  val failedResponse: String => Result = reason => Status(INTERNAL_SERVER_ERROR)(Json.toJson(Map("error"->"Invalid URL parameter",
    "reason" -> reason)))


  object TestController extends EmployeeFullTimeEquivalentController {
    override val authConnector = mockAuthConnector
    override val employeeFullTimeEquivalenceService = mock[EmployeeFullTimeEquivalenceService]
  }

  val fakeRequest = FakeRequest()

  before {
    reset(mockAuthConnector)
  }

  "EmployeeFullTimeEquivalenceController" should {
    "use the correct auth connector" in {
      EmployeeFullTimeEquivalentController.authConnector shouldBe AuthConnector
    }
  }

  "sending a GET request to the EmployeeFullTimeEquivalenceController" when {
    "Authenticated and enrolled" should {
      when(TestController.employeeFullTimeEquivalenceService.checkFullTimeEquivalence(Matchers.any())).thenReturn(successResponse(true))
      lazy val result = TestController.checkFullTimeEquivalence("25")(fakeRequest)
      "return status OK" in {
        setup()
        status(result) shouldBe OK
      }
      "return a JSON result" in {
        setup()
        contentType(result) shouldBe Some("application/json")
      }
    }


    "not Authenticated or enrolled" should {
      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        lazy val result = TestController.checkFullTimeEquivalence("25")(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }

    }
  }

}
