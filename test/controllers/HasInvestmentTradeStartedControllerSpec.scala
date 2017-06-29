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
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.UnitSpec

class HasInvestmentTradeStartedControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter {

  object TestController extends HasInvestmentTradeStartedController {
    override val authConnector = mockAuthConnector
  }

  before {
    reset(mockAuthConnector)
  }

  "HasInvestmentTradeStartedController" should {
    "use the correct auth connector" in {
      HasInvestmentTradeStartedController.authConnector shouldBe AuthConnector
    }
  }


  "validating the investment trade start date method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with a date more than four months in the past" should {
      val base = DateTime.now().minusMonths(5)
      lazy val result = TestController.validateHasInvestmentTradeStarted(base.dayOfMonth().get(),
        base.monthOfYear().get(),DateTime.now().year().get)(FakeRequest())

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

    "calling with a date less than four months in the past" should {

      lazy val result = TestController.validateHasInvestmentTradeStarted(DateTime.now().dayOfMonth().get(),
        DateTime.now().monthOfYear().get(),DateTime.now().year().get)(FakeRequest())

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

  "calling the validateHasInvestmentTradeStarted method with a TAVC account with status NotYetActivated and confidence level 50" should {

    "return status FORBIDDEN" in {
      setup("NotYetActivated")
      lazy val result = TestController.validateHasInvestmentTradeStarted(6,6,2017)(FakeRequest())
      status(result) shouldBe FORBIDDEN
    }
  }

}
