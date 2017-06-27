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

class GrossAssetsControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter {

  object TestController extends GrossAssetsController {
    override val authConnector = mockAuthConnector
  }

  val validAmount = 200000
  val invalidAmount = 200001
  val fakeRequest = FakeRequest()

  before {
    reset(mockAuthConnector)
  }
  
  "GrossAssetsController" should {
    "use the correct auth connector" in {
      GrossAssetsController.authConnector shouldBe AuthConnector
    }
  }
  

  "validating the checkGrossAssetsExceeded method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with an valid grossAmount which is exactly at the the maximum" should {

      lazy val result = TestController.checkGrossAssetsExceeded(validAmount)(fakeRequest)

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

    "calling with an invalid grossAmount which exceeds the maximum" should {

      lazy val result = TestController.checkGrossAssetsExceeded(invalidAmount)(fakeRequest)

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

  "validating the checkGrossAssetsExceeded method with a TAVC account with status NotYetActivated and confidence level 50" when  {

    "calling with a valid grossInvestment and a valid PreviousSchemesTotal" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkGrossAssetsExceeded(validAmount)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "calling with an invalid amount exceeding the maximum" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkGrossAssetsExceeded(invalidAmount)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }
  }

}
