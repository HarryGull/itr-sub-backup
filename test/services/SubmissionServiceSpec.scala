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

package services

import java.util.UUID

import connectors.SubmissionDESConnector
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import fixtures.SubmissionFixture
import org.mockito.Matchers
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.OneServerPerSuite
import org.mockito.Mockito._
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import uk.gov.hmrc.http.{ HeaderCarrier, HttpResponse }
import uk.gov.hmrc.http.logging.SessionId

class SubmissionServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite
  with SubmissionFixture {

  val sessionId = UUID.randomUUID.toString
  val mockSubmissionDESConnector : SubmissionDESConnector = mock[SubmissionDESConnector]
  val tavcRef = "XADD00000001234"
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

  object TestSubmissionService extends SubmissionService {
    val submissionDESConnector = mockSubmissionDESConnector
  }

  "SubmissionService" should {
    "Use SubmissionDESConnector" in {
      SubmissionService.submissionDESConnector shouldBe SubmissionDESConnector
    }
  }

  "SubmissionService.submitAA" should {

    lazy val result = TestSubmissionService.submitAA(validSubmissionJsValAA, tavcRef)

    "return the response from the DES connector" in {
      when(mockSubmissionDESConnector.submit(Matchers.eq(validSubmissionJsValAA), Matchers.eq(tavcRef))(Matchers.any(), Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(CREATED)))
      await(result).status shouldBe CREATED
    }
  }

    "SubmissionService.submitCS" should {

      lazy val result = TestSubmissionService.submitCS(validSubmissionJsValCS,tavcRef)

      "return the response from the DES connector" in {
        when(mockSubmissionDESConnector.submit(Matchers.eq(validSubmissionJsValCS),Matchers.eq(tavcRef))(Matchers.any(),Matchers.any()))
          .thenReturn(Future.successful(HttpResponse(CREATED)))
        await(result).status shouldBe CREATED
      }
  }

  "SubmissionService.getAASubmissionDetails" should {

    lazy val result = TestSubmissionService.getReturnsSummary(tavcRef)

    "return the response from the DES connector" in {
      when(mockSubmissionDESConnector.getReturnsSummary(Matchers.eq(tavcRef))(Matchers.any(),Matchers.any()))
        .thenReturn(Future.successful(HttpResponse(OK, Some(validSubmissionDetailsJsVal))))

     await(result) match {
       case response => {
         response.status shouldBe OK
       }
     }
    }
  }

}
