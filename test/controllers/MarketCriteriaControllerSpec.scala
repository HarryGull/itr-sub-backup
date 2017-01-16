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
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.OneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.UnitSpec

class MarketCriteriaControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter{

  object TestController extends MarketCriteriaController {
    override val authConnector = mockAuthConnector
  }

  val fakeRequest = FakeRequest()

  before {
    reset(mockAuthConnector)
  }

  "MarketCriteriaController" should {
    "use the correct auth connector" in {
      MarketCriteriaController.authConnector shouldBe AuthConnector
    }
  }

  "sending a GET request to the MarketCriteriaController" when {
    "Authenticated and enrolled" should {
      lazy val result = TestController.checkMarketCriteria(true,true)(fakeRequest)
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
        lazy val result = TestController.checkMarketCriteria(true,true)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }

    }
  }

}
