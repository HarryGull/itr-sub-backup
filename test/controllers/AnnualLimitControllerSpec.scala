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

class AnnualLimitControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter {

  object TestController extends AnnualLimitController {
    override val authConnector = mockAuthConnector
  }

  val maxAmount = 5000000

  val schemesTotal = maxAmount / 2
  val totalAmountRaised = maxAmount / 2
  val fakeRequest = FakeRequest()

  before {
    reset(mockAuthConnector)
  }

  "AnnualLimitController" should {
    "use the correct auth connector" in {
      AnnualLimitController.authConnector shouldBe AuthConnector
    }
  }


  "validating the checkLimitExceeded method with a total amount raised and previous schemes in range total" when {

    "the amounts combined are not above the maximum threshold check" should {

      lazy val result = TestController.checkLimitExceeded(schemesTotal, totalAmountRaised)(fakeRequest)

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
  }

  "validating the checkLimitExceeded method with a total amount raised and previous schemes in range total" when {

    "the amounts combined are above the maximum threshold check" should {

      lazy val result = TestController.checkLimitExceeded(schemesTotal, totalAmountRaised + 1)(fakeRequest)

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
        json.as[Boolean] shouldBe true
      }
    }
  }

  "validating the checkLimitExceeded method with a TAVC account with status NotYetActivated and confidence level 50" when {

    "calling with a KI = true. a valid ProposedInvestment and a valid PreviousSchemesTotal" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        lazy val result = TestController.checkLimitExceeded(schemesTotal, totalAmountRaised)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "calling with a KI = true. a valid ProposedInvestment and an invalid PreviousSchemesTotal" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        lazy val result = TestController.checkLimitExceeded(schemesTotal, totalAmountRaised)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }
  }


}
