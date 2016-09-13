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
import play.api.test.FakeRequest
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import controllers.SubmissionController
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.test.UnitSpec
import common.Constants._
import connectors.SubmissionDESConnector
import models.SubmissionResponseModel
import play.api.libs.json.JsValue
import play.api.mvc.{Action, BodyParser, BodyParsers}
import play.api.test
import services.SubmissionService
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class SubmissionControllerSpec extends UnitSpec with MockitoSugar {

  val sessionId = UUID.randomUUID.toString
  val mockHttp : WSHttp = mock[WSHttp]
  val mockSubmissionService = mock[SubmissionService]

  class Setup {
    object TestController extends SubmissionController {
      val submissionService = mockSubmissionService
    }

    object mockDESConnector extends SubmissionDESConnector {
      val serviceUrl = "dummy"
      val http = mockHttp
    }
  }

//  "The controller should return a  " should {
//    "OK" in new Setup {
//
//      when(mockHttp.POST[JsValue, HttpResponse](Matchers.anyString(), Matchers.any(), Matchers.any())(Matchers.any(), Matchers.any(), Matchers.any()))
//        .thenReturn(HttpResponse(OK))
//
//
//      when(submissionService.submitAA(dummySubmissionRequestModelValid)(Matchers.any(), Matchers.any())).thenReturn(Future.successful(HttpResponse(OK)))
//
//      val result = TestController.submitAA().apply(FakeRequest().withBody(dummySubmissionRequestModelValid)).run
//
//      status(result) shouldBe OK
//    }
//  }

//  "The stub should return a bad request if the email contains the text badrequest " should {
//    "return a json package detailing the status" in new Setup {
//
//      val result = TestController.submitAdvancedAssuranceApplication().apply(FakeRequest().withBody(badRequestJs))
//      status(result) shouldBe BAD_REQUEST
//    }
//  }
//
//
//  "The stub should return an internal server error if the email contains the text internalservererror " should {
//    "return a json package detailing the status" in new Setup {
//
//      val result = TestController.submitAdvancedAssuranceApplication().apply(FakeRequest().withBody(internalServerErrorJs))
//      status(result) shouldBe INTERNAL_SERVER_ERROR
//    }
//  }
//
//  "The stub should return a service unavailable error if the email contains the text serviceunavailable " should {
//    "return a json package detailing the status" in new Setup {
//
//      val result = TestController.submitAdvancedAssuranceApplication().apply(FakeRequest().withBody(serviceUnavilableJs))
//      status(result) shouldBe SERVICE_UNAVAILABLE
//    }
//  }
//
//  "The stub should return a service unavailable error if the email contains the text forbidden " should {
//    "return a json package detailing the status" in new Setup {
//
//      val result = TestController.submitAdvancedAssuranceApplication().apply(FakeRequest().withBody(forbiddenJs))
//      status(result) shouldBe FORBIDDEN
//    }
//  }

}
