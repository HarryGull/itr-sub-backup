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
import org.scalatest.BeforeAndAfter
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class SeisAllowanceControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter {

  object TestController extends SeisAllowanceController {
    override val authConnector = mockAuthConnector
  }

  val validAmountMax = 150000
  val invalidAmountBoundary = 150001
  val fakeRequest = FakeRequest()

  before {
    reset(mockAuthConnector)
  }
  
  "SeisAllowanceController" should {
    "use the correct auth connector" in {
      SeisAllowanceController.authConnector shouldBe AuthConnector
    }
  }

  "validating the checkSeisAllowanceExceeded method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with a valid maximum valid PreviousSchemesTotalSinceStartDate amount" should {

      lazy val result = TestController.checkSeisAllowanceExceeded(validAmountMax)(fakeRequest)

      "return status OK" in {
        setup()
        status(result) shouldBe OK
      }

      "return a JSON result" in {
        setup()
        contentType(result) shouldBe Some("application/json")
      }

      "return false" in {
        setup()
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe false
      }
    }

    "calling with an invalid PreviousSchemesTotalSinceStartDate amount at the boundary" should {

      lazy val result = TestController.checkSeisAllowanceExceeded(invalidAmountBoundary)(fakeRequest)

      "return status OK" in {
        setup()
        status(result) shouldBe OK
      }

      "return a JSON result" in {
        setup()
        contentType(result) shouldBe Some("application/json")
      }

      "return true" in {
        setup()
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe true
      }
    }
  }

  "validating the checkSeisAllowanceExceeded method with a TAVC account with status NotYetActivated and confidence level 50" when  {

    "calling with with a valid PreviousSchemesTotalSinceStartDate" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkSeisAllowanceExceeded(validAmountMax)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "calling with with an invalid PreviousSchemesTotalSinceStartDate" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkSeisAllowanceExceeded(invalidAmountBoundary)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }
  }

}
