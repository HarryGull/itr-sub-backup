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
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.{OneAppPerTest, OneAppPerSuite}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito._
import uk.gov.hmrc.play.test.UnitSpec

class TradeStartDateControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter {

  object TestController extends TradeStartDateController {
    override val authConnector = mockAuthConnector
  }

  before {
    reset(mockAuthConnector)
  }

  "TradeStartDateController" should {
    "use the correct auth connector" in {
      TradeStartDateController.authConnector shouldBe AuthConnector
    }
  }


  "validating the trade start date method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with a date more than two years in the past" should {

      lazy val result = TestController.validateTradeStartDate(DateTime.now().dayOfMonth().get(),
        DateTime.now().monthOfYear().get(),DateTime.now().minusYears(3).year().get)(FakeRequest())

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

    "calling with a date less than two years in the past" should {

      lazy val result = TestController.validateTradeStartDate(DateTime.now().dayOfMonth().get(),
        DateTime.now().monthOfYear().get(),DateTime.now().minusYears(1).year().get)(FakeRequest())

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

  "validating the checkLifetimeAllowanceExceeded method with a TAVC account with status NotYetActivated and confidence level 50" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        lazy val result = TestController.validateTradeStartDate(1,1,2016)(FakeRequest())
        status(result) shouldBe FORBIDDEN
      }
  }

}
