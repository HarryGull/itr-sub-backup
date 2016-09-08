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

package contollers

import controllers.KnowledgeIntensiveController._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class KnowledgeIntensiveControllerSpec extends UnitSpec with WithFakeApplication{

  val rAndDCostsZero = List(100, 100, 100, 0, 0, 0)
  val rAndDCostsTen = List(100, 100, 100, 10, 10, 10)

  val fakeRequest = FakeRequest()

  "validating the checkKICosts method" when  {

    "calling with R and D costs all 0" should {

      val result = checkKICosts(rAndDCostsZero(0),rAndDCostsZero(1),rAndDCostsZero(2),
        rAndDCostsZero(3),rAndDCostsZero(4),rAndDCostsZero(5))(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return false" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe false
      }
    }

    "calling with R and D costs all above 10" should {


      val result = checkKICosts(rAndDCostsTen(0),rAndDCostsTen(1),rAndDCostsTen(2),
        rAndDCostsTen(3),rAndDCostsTen(4),rAndDCostsTen(5))(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return true" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe true
      }
    }
  }

  "validating the checkSecondaryConditions method" when {

    "calling with (true,true)" should {

      val result = checkSecondaryConditions(true, true)(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return true" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe true
      }
    }

    "calling with (false,false)" should {

      val result = checkSecondaryConditions(false, false)(fakeRequest)

      "return status 200" in {
        status(result) shouldBe 200
      }

      "return a JSON result" in {
        contentType(result) shouldBe Some("application/json")
        charset(result) shouldBe Some("utf-8")
      }

      "return false" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[Boolean] shouldBe false
      }
    }
  }
}