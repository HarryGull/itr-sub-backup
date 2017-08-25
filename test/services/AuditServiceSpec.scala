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

import common.{AuditConstants, ResponseConstants}
import helpers.{AuditHelper, EtmpResponseReasons}
import org.mockito.{ArgumentCaptor, Matchers}
import org.mockito.Matchers.{eq => eqTo, _}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.http.logging.SessionId
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import uk.gov.hmrc.play.config.AppName
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec
import metrics.{Metrics, MetricsEnum}
import org.scalatest.BeforeAndAfter
import org.scalatest.Inside._
import org.scalatest.concurrent.Eventually
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.audit.EventKeys
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditServiceSpec extends UnitSpec with MockitoSugar with AppName with OneAppPerSuite with BeforeAndAfter with AuditHelper with Eventually {

  val auditMock = mock[AuditConnector]

  val metricsMock = mock[Metrics]
  val auditMockResponse = mock[(ExtendedDataEvent) => Unit]
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId("testID")))
  implicit val rh: RequestHeader = FakeRequest("GET", testRequestPath)


  object TestAuditService extends AuditService with AppName {
    override val auditConnector = auditMock
    override val metrics = metricsMock
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val rh: RequestHeader = FakeRequest("GET", testRequestPath)

    //when(auditMock.sendEvent(hc).thenReturn(auditMockResponse)
    when(auditMock.sendEvent(Matchers.any())(Matchers.any(), Matchers.any())).thenReturn(Future.successful(AuditResult.Success))

  }

  object TestAuditServiceWithCustomLogFormat extends AuditService with AppName {
    //override val audit = auditMock
    override val auditConnector = auditMock
    override val metrics = metricsMock
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val rh: RequestHeader = FakeRequest("GET", testRequestPath)

    // override the logging message format to customise and test it
    override val logMessageFormat = (controller: String, controllerAction: String, dayOfWeek: String, statusCode: String, eventMessage: String) =>
      s"Subscribe Audit event recored on $dayOfWeek for [${controller + "/" + controllerAction}]. StatusCode was: [$statusCode]. Event Status is: $eventMessage"

    val expectedCustomLogFormatSuccessMessageFriday10Count =
      "Subscribe Audit event recored on Friday for [SubmissionController/subscribe]. StatusCode was: [200]. Event Status is: Success"
  }


  before {
    reset(metricsMock)
  }

  /**AA Logging tests**/

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is Created with no content" should {
      "log the expected message and call the incrementSuccessCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseCreatedNocontent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.success
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseCreatedNocontent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementSuccessCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is OK with no content" should {
      "log the expected message and call the incrementSuccessCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseNonContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.success
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseNonContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementSuccessCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is OK with Etmp response message" should {
      "log the expected message and call the incrementSuccessCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseOkSuccess,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.success
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseOkSuccess.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementSuccessCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is bad request with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseBadRequestNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultBadRequest
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseBadRequestNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is bad request with Etmp response content duplicate" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseBadRequestEtmpDuplicate,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.duplicateSubmission400
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseBadRequestEtmpDuplicate.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is not found with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseNotFoundNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultNotFound
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseNotFoundNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is server unavailable with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseServiceUnavailableNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultServiceUnavailable
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseServiceUnavailableNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is server unavailable with Etmp response not processed" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseServiceUnavailableEtmpNotProcessed,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.notProcessed503
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseServiceUnavailableEtmpNotProcessed.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is internal server error with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseInternalServerErrorNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultInternalServerError
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is internal server error with Etmp sap error response" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseInternalServerErrorEtmpSap,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.sapError500
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorEtmpSap.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscription" when {
    "the status is internal server error with Etmp response" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseInternalServerErrorEtmp,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.serverError500
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorEtmp.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscription" when {
    "the status is internal server error with Etmp no regime" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseInternalServerErrorEtmpRegime,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.noRegime500
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorEtmpRegime.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseAA " when {
    "the status is other error with success content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseAA(responseOtherErrorNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultOther
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseOtherErrorNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION)
      }
    }
  }


  /**CS Logging tests**/

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is Created with no content" should {
      "log the expected message and call the incrementSuccessCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseCreatedNocontent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.success
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseCreatedNocontent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementSuccessCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is OK with no content" should {
      "log the expected message and call the incrementSuccessCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseNonContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.success
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseNonContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementSuccessCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is OK with Etmp response message" should {
      "log the expected message and call the incrementSuccessCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseOkSuccess,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.success
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseOkSuccess.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementSuccessCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is bad request with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseBadRequestNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultBadRequest
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseBadRequestNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is bad request with Etmp response content duplicate" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseBadRequestEtmpDuplicate,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.duplicateSubmission400
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseBadRequestEtmpDuplicate.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is not found with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseNotFoundNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultNotFound
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseNotFoundNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is server unavailable with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseServiceUnavailableNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultServiceUnavailable
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseServiceUnavailableNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is server unavailable with Etmp response not processed" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseServiceUnavailableEtmpNotProcessed,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.notProcessed503
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseServiceUnavailableEtmpNotProcessed.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is internal server error with no content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseInternalServerErrorNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultInternalServerError
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS" when {
    "the status is internal server error with Etmp sap error response" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseInternalServerErrorEtmpSap,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.sapError500
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorEtmpSap.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionAA" when {
    "the status is internal server error with Etmp response" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseInternalServerErrorEtmp,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.serverError500
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorEtmp.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionCS" when {
    "the status is internal server error with Etmp no regime" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseInternalServerErrorEtmpRegime,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = EtmpResponseReasons.noRegime500
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseInternalServerErrorEtmpRegime.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  "Calling AuditService.logSubscriptionResponseCS " when {
    "the status is other error with success content" should {
      "log the expected message and call the incrementFailedCounter metric" in {
        val loggedMessage = TestAuditService.logSubscriptionResponseCS(responseOtherErrorNoContent,
          submissionControllerTestName, subscribeTestAction, safeId)
        val expectedReason = ResponseConstants.defaultOther
        loggedMessage shouldBe TestAuditService.logMessageFormat(submissionControllerTestName,
          subscribeTestAction, safeId, responseOtherErrorNoContent.status.toString, expectedReason)

        verify(metricsMock, times(1)).incrementFailedCounter(MetricsEnum.TAVC_SUBMISSION_CS)
      }
    }
  }

  //TODO: (AA) trry to improve the below with an event capture - not managed so far..
  "Calling AuditService.sendTAVCAdvancedAssuranceEvent and status No conten" when {
    "the request body SubscriptionType is fully populated" should {
      "perform the underlying TXM explicit audit with the actualDataEvent populated as expected without error" in {
        TestAuditService.sendTAVCAdvancedAssuranceEvent(fullAuditDataAA, tavcRefNumber,
          responseNonContent, acknowledgementReference)
      }
    }
  }

  "Calling AuditService.sendTAVCAdvancedAssuranceEvent and status OK" when {
    "the request body SubscriptionType is fully populated" should {
      "perform the underlying TXM explicit audit with the actualDataEvent populated as expected without error" in {
        TestAuditService.sendTAVCAdvancedAssuranceEvent(fullAuditDataAA, tavcRefNumber,
          responseBadRequestEtmpDuplicate, acknowledgementReference)
      }
    }
  }

  "Calling AuditService.sendTAVCAdvancedAssuranceEvent and status Internal server Error with no content" when {
    "the request body SubscriptionType is fully populated" should {
      "perform the underlying TXM explicit audit with the actualDataEvent populated as expected wihtout error" in {
        TestAuditService.sendTAVCAdvancedAssuranceEvent(fullAuditDataAA, tavcRefNumber,
          responseInternalServerErrorNoContent, acknowledgementReference)


      }
    }
  }


  //TODO: (CS)trry to improve the below with an event capture - not managed so far..
  "Calling AuditService.sendTAVCSubmitComplianceStatementEvent and status No conten" when {
    "the request body SubscriptionType is fully populated" should {
      "perform the underlying TXM explicit audit with the actualDataEvent populated as expected without error" in {
        TestAuditService.sendTAVCSubmitComplianceStatementEvent(fullAuditDataCS, tavcRefNumber,
          responseNonContent, acknowledgementReference)
      }
    }
  }

  "Calling AuditService.sendTAVCSubmitComplianceStatementEvent and status OK" when {
    "the request body SubscriptionType is fully populated" should {
      "perform the underlying TXM explicit audit with the actualDataEvent populated as expected without error" in {
        TestAuditService.sendTAVCSubmitComplianceStatementEvent(fullAuditDataCS, tavcRefNumber,
          responseBadRequestEtmpDuplicate, acknowledgementReference)
      }
    }
  }

  "Calling AuditService.sendTAVCSubmitComplianceStatementEvent and status Internal server Error with no content" when {
    "the request body SubscriptionType is fully populated" should {
      "perform the underlying TXM explicit audit with the actualDataEvent populated as expected wihtout error" in {
        TestAuditService.sendTAVCSubmitComplianceStatementEvent(fullAuditDataCS, tavcRefNumber,
          responseInternalServerErrorNoContent, acknowledgementReference)


      }
    }
  }


}
