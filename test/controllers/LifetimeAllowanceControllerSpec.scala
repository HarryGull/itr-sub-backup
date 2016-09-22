/*
 * Copyright 2016 HM Revenue & Customs
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
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class LifetimeAllowanceControllerSpec extends UnitSpec with WithFakeApplication with BeforeAndAfter {

  object TestController extends LifetimeAllowanceController {
    override val authConnector = mockAuthConnector
  }

  val validAmount = 1
  val invalidAmount = 999999999
  val fakeRequest = FakeRequest()

  before {
    reset(mockAuthConnector)
  }
  
  "LifetimeAllowanceController" should {
    "use the correct auth connector" in {
      LifetimeAllowanceController.authConnector shouldBe AuthConnector
    }
  }
  

  "validating the checkLifetimeAllowanceExceeded method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with a KI = true. a valid ProposedInvestment and a valid PreviousSchemesTotal" should {

      lazy val result = TestController.checkLifetimeAllowanceExceeded(true, true, validAmount,validAmount)(fakeRequest)

      "return status OK" in {
        setup()
        status(result) shouldBe OK
      }

      "return a JSON result" in {
        setup()
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return false" in {
        setup()
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe false
      }
    }

    "calling with a KI = true. a valid ProposedInvestment and an invalid PreviousSchemesTotal" should {

      lazy val result = TestController.checkLifetimeAllowanceExceeded(true, true, invalidAmount,validAmount)(fakeRequest)

      "return status OK" in {
        setup()
        status(result) shouldBe OK
      }

      "return a JSON result" in {
        setup()
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return true" in {
        setup()
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe true
      }
    }
  }

  "validating the checkLifetimeAllowanceExceeded method with a TAVC account with status NotYetActivated and confidence level 50" when  {

    "calling with a KI = true. a valid ProposedInvestment and a valid PreviousSchemesTotal" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkLifetimeAllowanceExceeded(true, true, validAmount,validAmount)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "calling with a KI = true. a valid ProposedInvestment and an invalid PreviousSchemesTotal" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkLifetimeAllowanceExceeded(true, true, invalidAmount,validAmount)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }
  }

}
