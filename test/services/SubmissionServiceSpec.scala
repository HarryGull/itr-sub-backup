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

package services

import java.util.UUID

import connectors.SubmissionDESConnector
import uk.gov.hmrc.play.http.HeaderCarrier
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import fixtures.SubmissionFixture
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.OneServerPerSuite
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmissionServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite
  with SubmissionFixture{

  val sessionId = UUID.randomUUID.toString
  val mockHttp : WSHttp = mock[WSHttp]
  val mockSubmissionDESConnector : SubmissionDESConnector = mock[SubmissionDESConnector]

  val tavcRef = "XADD00000001234"

  class Setup {
    object TestSubmissionService extends SubmissionService {
      val submissionDESConnector = mockSubmissionDESConnector
      val http = mockHttp
    }
  }

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

  "The submission service should" should {
    "return a valid response" in new Setup {

//  when(mockHttp.POST[JsValue, HttpResponse](Matchers.any(), Matchers.any())(Matchers.any(),
      // Matchers.any(), Matchers.any()))
//        .thenReturn(Future.successful(HttpResponse(CREATED)))
//
//      val result = TestSubmissionService.submitAA(Matchers.any(), Matchers.any())
//
//      await(result).status shouldBe CREATED
    }
  }

}