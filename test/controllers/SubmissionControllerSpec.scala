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
import play.api.libs.json.{JsValue, Json}
import play.api.test.{FakeHeaders, FakeRequest}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import fixtures.SubmissionFixture
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.OneAppPerSuite
import services.{AuditService, SubmissionService}

import scala.concurrent.Future

class SubmissionControllerSpec extends UnitSpec with MockitoSugar with OneAppPerSuite with BeforeAndAfter with SubmissionFixture {

  val mockSubmissionService = mock[SubmissionService]
  val mockAuditService = mock[AuditService]

  val malformedJson =
    """
      |{
      |{
      |  "statusCode": malformed,
      |  "message": "malformed"}'"
      |}
    """.stripMargin

  val acknowledgemenRefJs =

    """{
                              |"acknowledgementReference": "XDTAVC000544444",
                              |	"submissionType": {
                              |		"agentReferenceNumber": "AARN1234567",
                              |		"correspondenceDetails": {
                              |			"contactName": {
                              |				"name1": "nameOne",
                              |				"name2": "nameTwo"
                              |			},
                              |			"contactDetails": {
                              |				"phoneNumber": "00000000001",
                              |				"mobileNumber": "00000000002",
                              |				"faxNumber": "00000000003",
                              |				"emailAddress": "test@test.com"
                              |			},
                              |			"contactAddress": {
                              |				"addressLine1": "addressOne",
                              |				"addressLine2": "addressTwo",
                              |				"addressLine3": "addressThree",
                              |				"addressLine4": "addressFour",
                              |				"postalCode": "AA1 1AA",
                              |				"countryCode": "GB"
                              |			}
                              |		}
                              |	}
                              |}""".stripMargin

  val acknowledgemenRefJsVal = Json.parse(acknowledgemenRefJs)

  implicit val hc = HeaderCarrier()

  class Setup(status: Int, response: JsValue) {
    when(mockSubmissionService.submitAA(Matchers.any(),Matchers.any())(Matchers.any(), Matchers.any()))
      .thenReturn(Future.successful(HttpResponse(status, Some(response))))
    object TestController extends SubmissionController {
      override val submissionService = mockSubmissionService
      override val authConnector = mockAuthConnector
      override val auditService = mockAuditService
    }
  }

  before {
    reset(mockAuthConnector)
  }

  "SubmissionController" should {
    "use the correct auth connector" in {
      SubmissionController.authConnector shouldBe AuthConnector
    }
    "use the correct submission service" in {
      SubmissionController.submissionService shouldBe SubmissionService
    }
  }

  "SubmissionController.submitAA with a TAVC account with status Activated and confidence level 50" when {

    "submitAA is called" should {

      "return an Created when a CREATED response is returned from stub" in new Setup(CREATED, validSubmissionJsVal) {
        setup()
        val result = TestController.submitAA(tavcRef)(FakeRequest().withBody(validSubmissionJsVal))
        status(result) shouldBe CREATED
      }

      "return a Bad request if valid JSON already contains an acknowledgementReference" in new Setup(CREATED, acknowledgemenRefJsVal) {
        setup()
        val result = TestController.submitAA(tavcRef).apply(FakeRequest().withBody(acknowledgemenRefJsVal))
        status(result) shouldBe BAD_REQUEST
      }

      "return a Forbidden when a Forbidden response is returned from stub" in new Setup(FORBIDDEN, validSubmissionJsVal) {
        setup()
        val result = TestController.submitAA(tavcRef).apply(FakeRequest().withBody(validSubmissionJsVal))
        status(result) shouldBe FORBIDDEN
      }


      "return a BadRequest when a Bad Request response is returned from stub" in new Setup(BAD_REQUEST, validSubmissionJsVal) {
        setup()
        val result = TestController.submitAA(tavcRef).apply(FakeRequest().withBody(validSubmissionJsVal))
        status(result) shouldBe BAD_REQUEST
      }

      "return a ServiceUnavailable when a ServiceUnavailable is returned from stub" in new Setup(SERVICE_UNAVAILABLE, validSubmissionJsVal) {
        setup()
        val result = TestController.submitAA(tavcRef).apply(FakeRequest().withBody(validSubmissionJsVal))
        status(result) shouldBe SERVICE_UNAVAILABLE
      }

      "return an Internal Server error when any other response is returned from stub" in new Setup(INTERNAL_SERVER_ERROR, validSubmissionJsVal) {
        setup()
        val result = TestController.submitAA(tavcRef).apply(FakeRequest().withBody(validSubmissionJsVal))
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "SubmissionController.submitAA with a TAVC account with status NotYetActivated and confidence level 50" when {

    "submitAA is called" should {

      "return a FORBIDDEN response" in new Setup(CREATED, validSubmissionJsVal) {
        setup("NotYetActivated")
        val result = TestController.submitAA(tavcRef)(FakeRequest().withBody(validSubmissionJsVal))
        status(result) shouldBe FORBIDDEN
      }
    }
  }

}
