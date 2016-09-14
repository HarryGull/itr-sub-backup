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

/**
  * Copyright 2016 HM Revenue & Customs
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package contollers

import java.util.UUID

import controllers.SubmissionController._
import play.api.libs.json.Json
import play.api.test.{FakeRequest}
import uk.gov.hmrc.play.http.{HttpResponse}
import controllers.SubmissionController
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.test.{WithFakeApplication, UnitSpec}
import common.Constants._
import models.{SubmissionResponseModel}
import services.SubmissionService


import scala.concurrent.Future

class SubmissionControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication{

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




  class Setup {
    object TestController extends SubmissionController {
      val submissionService = mockSubmissionService
    }
  }

  "The controller should return a  " should {
    "OK when a CREATED response is returned from stub" in new Setup {

      when(mockSubmissionService.submitAA(Matchers.any())(Matchers.any(), Matchers.any()))
          .thenReturn(Future.successful(HttpResponse(CREATED,Some(validJs))))

      val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelValid)))
      status(result) shouldBe OK
    }

    "Forbidden when a Forbidden response is returned from stub" in new Setup {

      when(mockSubmissionService.submitAA(Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(FORBIDDEN,Some(Json.toJson(forbiddenJs)))))

      val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelForbidden)))
      status(result) shouldBe FORBIDDEN
    }


    "BadRequest when a Bad Request response is returned from stub" in new Setup {

      when(mockSubmissionService.submitAA(Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(BAD_REQUEST,Some(Json.toJson(badRequestJs)))))

      val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelBad)))
      status(result) shouldBe BAD_REQUEST
    }


    "ServiceUnavailable when a ServiceUnavailable is returned from stub" in new Setup {

      when(mockSubmissionService.submitAA(Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(SERVICE_UNAVAILABLE,Some(Json.toJson(serviceUnavilableJs)))))

      val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelServiceUnavailable)))
      status(result) shouldBe SERVICE_UNAVAILABLE
    }

    "Internal Server error when any other response is returned from stub" in new Setup {

      when(mockSubmissionService.submitAA(Matchers.any())(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(INTERNAL_SERVER_ERROR,Some(Json.toJson(internalServerErrorJs)))))

      val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(dummySubmissionRequestModelInternalServerError)))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }

    "Bad request with malformed JSON" in new Setup {

      val result = TestController.submitAA().apply(FakeRequest().withBody(Json.toJson(malformedJson)))
      status(result) shouldBe BAD_REQUEST
    }
  }



}
