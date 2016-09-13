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

package services

import java.util.UUID

import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneServerPerSuite
import uk.gov.hmrc.play.test.UnitSpec
import common.Constants._
import connectors.SubmissionDESConnector
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.logging.SessionId
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class SubmissionServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite {

  val sessionId = UUID.randomUUID.toString
  val mockHttp : WSHttp = mock[WSHttp]
  val mockSubmissionDESConnector : SubmissionDESConnector = mock[SubmissionDESConnector]

  class Setup {
    object TestSubmissionService extends SubmissionService {
      val submissionDESConnector = mockSubmissionDESConnector
      val http = mockHttp
    }
  }

  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

  "The submission service should" should {
    "return a valid response" in new Setup {

      when(mockSubmissionDESConnector.submitAA(Matchers.eq(dummySubmissionRequestModelValid))(Matchers.any(),Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(CREATED)))

      val result = TestSubmissionService.submitAA(dummySubmissionRequestModelValid)

      await(result).status shouldBe CREATED
    }
  }

}
