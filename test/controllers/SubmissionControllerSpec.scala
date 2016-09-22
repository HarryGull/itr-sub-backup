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

import helpers.AuthHelper._
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import common.Constants._
import models.SubmissionResponseModel
import org.scalatest.BeforeAndAfter
import services.SubmissionService

import scala.concurrent.Future

class SubmissionControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication with BeforeAndAfter {

  val mockSubmissionService = mock[SubmissionService]
  val submissionResponse = SubmissionResponseModel(true,"FBUND09889765", "Submission Request Successful")

  val malformedJson =
    """
      |{
      |{
      |  "statusCode": malformed,
      |  "message": "malformed"}'"
      |}
    """.stripMargin

  implicit val hc = HeaderCarrier()

  class Setup(status: Int, response: JsValue) {
    when(mockSubmissionService.submitAA(Matchers.any())(Matchers.any(), Matchers.any()))
      .thenReturn(Future.successful(HttpResponse(status, Some(response))))
    object TestController extends SubmissionController {
      override val submissionService = mockSubmissionService
      override val authConnector = mockAuthConnector
    }
  }

  before {
    reset(mockAuthConnector)
  }

  "SubmissionController.submitAA with a TAVC account with status Activated and confidence level 50" when {

    "submitAA is called" should {

      "return an OK when a CREATED response is returned from stub" in new Setup(CREATED, validJs) {
        setup()
        val result = TestController.submitAA()(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelValid)))
        status(result) shouldBe OK
      }

      "return a Forbidden when a Forbidden response is returned from stub" in new Setup(FORBIDDEN, Json.toJson(forbiddenJs)) {
        setup()
        val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelForbidden)))
        status(result) shouldBe FORBIDDEN
      }


      "return a BadRequest when a Bad Request response is returned from stub" in new Setup(BAD_REQUEST, Json.toJson(badRequestJs)) {
        setup()
        val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelBad)))
        status(result) shouldBe BAD_REQUEST
      }


      "return a ServiceUnavailable when a ServiceUnavailable is returned from stub" in new Setup(SERVICE_UNAVAILABLE, Json.toJson(serviceUnavilableJs)) {
        setup()
        val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelServiceUnavailable)))
        status(result) shouldBe SERVICE_UNAVAILABLE
      }

      "return an Internal Server error when any other response is returned from stub" in new Setup(INTERNAL_SERVER_ERROR, Json.toJson(internalServerErrorJs)) {
        setup()
        val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelInternalServerError)))
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }

      "return a Bad request with malformed JSON" in new Setup(CREATED, validJs) {
        setup()
        val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(malformedJson)))
        status(result) shouldBe BAD_REQUEST
      }
    }
  }

  "SubmissionController.submitAA with a TAVC account with status NotYetActivated and confidence level 50" when {

    "submitAA is called" should {

      "return a FORBIDDEN response" in new Setup(CREATED, validJs) {
        setup("NotYetActivated")
        val result = TestController.submitAA()(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelValid)))
        status(result) shouldBe FORBIDDEN
      }
    }
  }

}
