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
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import org.mockito.Mockito._
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import common.MockConstants._

class ShareIssueDateControllerSpec extends UnitSpec with OneAppPerSuite with BeforeAndAfter {

  object TestController extends SubmissionPeriodController {
    override val authConnector = mockAuthConnector
  }

  val invalidMonth = 13
  val today = DateTime.now()
  val validDateSuccess = new DateTime(today.minusYears(1).getYear, today.getMonthOfYear, today.getDayOfMonth, 0 , 0)
  val validDateFailure = new DateTime(today.minusYears(3).getYear, today.getMonthOfYear, today.getDayOfMonth, 0 , 0)

  val fakeRequest = FakeRequest()

  before {
    reset(mockAuthConnector)
  }

  "ShareIssueDateController" should {
    "use the correct auth connector" in {
      SubmissionPeriodController.authConnector shouldBe AuthConnector
    }
  }


  "validating the dateOfShareIssueCheck method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with valid dates that pass the check" should {

      lazy val result = {
        TestController.submissionPeriodCheck(validDateSuccess.getDayOfMonth, validDateSuccess.getMonthOfYear, validDateSuccess.getYear,
                                             validDateSuccess.getDayOfMonth, validDateSuccess.getMonthOfYear, validDateSuccess.getYear)(fakeRequest)
      }

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

    "calling with valid dates that fail the check" should {

      lazy val result = {
        TestController.submissionPeriodCheck(validDateFailure.getDayOfMonth, validDateFailure.getMonthOfYear, validDateFailure.getYear,
          validDateFailure.getDayOfMonth, validDateFailure.getMonthOfYear, validDateFailure.getYear)(fakeRequest)
      }

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


  "Validating the dateOfShareIssueCheck method with a TAVC account with status Activated and confidence level 50" when  {

    "calling with an Invalid date" should {

      lazy val result = TestController.submissionPeriodCheck(validDateSuccess.getDayOfMonth, invalidMonth, validDateSuccess.getYear,
        validDateSuccess.getDayOfMonth, invalidMonth, validDateSuccess.getYear)(fakeRequest)

      "return a BadRequest with an error reason" in {
        setup()
        status(result) shouldBe BAD_REQUEST
      }

      "return a JSON result" in {
        setup()
        contentType(result) shouldBe Some("application/json")
      }
    }
  }

  "validating the dateOfShareIssueCheck method with a TAVC account with status NotYetActivated and confidence level 50" when  {

    "calling with a dates that pass the check" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        lazy val result = {
          TestController.submissionPeriodCheck(validDateSuccess.getDayOfMonth, validDateSuccess.getMonthOfYear, validDateSuccess.getYear,
            validDateSuccess.getDayOfMonth, validDateSuccess.getMonthOfYear, validDateSuccess.getYear)(fakeRequest)
        }
        status(result) shouldBe FORBIDDEN
      }
    }

    "calling with an dates that fail the check" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        lazy val result = {
          TestController.submissionPeriodCheck(validDateFailure.getDayOfMonth, validDateFailure.getMonthOfYear, validDateFailure.getYear,
            validDateFailure.getDayOfMonth, validDateFailure.getMonthOfYear, validDateFailure.getYear)(fakeRequest)
        }
        status(result) shouldBe FORBIDDEN
      }
    }

    "calling with invalid dates" should {

      "return status FORBIDDEN" in {
        setup("NotYetActivated")
        lazy val result = TestController.submissionPeriodCheck(validDateSuccess.getDayOfMonth, invalidMonth, validDateSuccess.getYear,
          validDateSuccess.getDayOfMonth, invalidMonth, validDateSuccess.getYear)(fakeRequest)
        status(result) shouldBe FORBIDDEN
      }
    }
  }


}
