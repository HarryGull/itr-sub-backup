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
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.OneAppPerSuite

class KnowledgeIntensiveControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter {

  object TestController extends KnowledgeIntensiveController {
    override val authConnector = mockAuthConnector
  }

  val rAndDCostsZero = List(100, 100, 100, 0, 0, 0)
  val rAndDCostsTen = List(100, 100, 100, 10, 10, 10)
  val fakeRequest = FakeRequest()

  before{
    reset(mockAuthConnector)
  }

  "KnowledgeIntensiveController" should {
    "use the correct auth connector" in {
      KnowledgeIntensiveController.authConnector shouldBe AuthConnector
    }
  }

  "validating the checkKICosts method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with R and D costs all 0" should {


      lazy val result = TestController.checkKICosts(rAndDCostsZero(0),rAndDCostsZero(1),rAndDCostsZero(2),
        rAndDCostsZero(3),rAndDCostsZero(4),rAndDCostsZero(5))(fakeRequest)

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

    "calling with R and D costs all above 10" should {

      lazy val result = TestController.checkKICosts(rAndDCostsTen(0),rAndDCostsTen(1),rAndDCostsTen(2),
        rAndDCostsTen(3),rAndDCostsTen(4),rAndDCostsTen(5))(fakeRequest)

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

  "validating the checkSecondaryConditions method with a TAVC account with status Activated and confidence level 50" when {

    "calling with (true,true)" should {

      lazy val result = TestController.checkSecondaryConditions(true, true)(fakeRequest)

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

    "calling with (false,false)" should {

      lazy val result = TestController.checkSecondaryConditions(false, false)(fakeRequest)

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

  "validating the checkKICosts method with a TAVC account with status NotYetActivated and confidence level 50" when  {

    "calling with R and D costs all 0" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkKICosts(rAndDCostsZero(0),rAndDCostsZero(1),rAndDCostsZero(2),
          rAndDCostsZero(3),rAndDCostsZero(4),rAndDCostsZero(5))(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }

    }

    "calling with R and D costs all above 10" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkKICosts(rAndDCostsTen(0),rAndDCostsTen(1),rAndDCostsTen(2),
          rAndDCostsTen(3),rAndDCostsTen(4),rAndDCostsTen(5))(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }

    }
  }

  "validating the checkSecondaryConditions method with a TAVC account with status NotYetActivated and confidence level 50" when {

    "calling with (true,true)" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkSecondaryConditions(true, true)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }

    "calling with (false,false)" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        val result = TestController.checkSecondaryConditions(false, false)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }
  }
}
