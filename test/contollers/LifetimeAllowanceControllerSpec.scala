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

import controllers.LifetimeAllowanceController._
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class LifetimeAllowanceControllerSpec extends UnitSpec with WithFakeApplication{

  val validAmount = 1
  val invalidAmount = 999999999

  val fakeRequest = FakeRequest()

  "validating the checkLifetimeAllowanceExceeded method" when  {

    "calling with a KI = true. a valid ProposedInvestment and a valid PreviousSchemesTotal" should {

      val result = checkLifetimeAllowanceExceeded(true, true, validAmount,validAmount)(fakeRequest)

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

    "calling with a KI = true. a valid ProposedInvestment and an invalid PreviousSchemesTotal" should {

      val result = checkLifetimeAllowanceExceeded(true, true, invalidAmount,validAmount)(fakeRequest)

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
}
