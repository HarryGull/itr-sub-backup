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

import common.{CSAuditConstants, AAAuditConstants, AuditConstants, ResponseConstants}
import config.MicroserviceAuditConnector
import metrics.MetricsEnum
import metrics.MetricsEnum.MetricsEnum
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.config.AppName
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.AuditExtensions.auditHeaderCarrier
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector, AuditResult}
import play.api.http.Status
import play.api.Logger
import metrics.{Metrics, MetricsEnum}
import models._
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success, Try}

object AuditService extends AuditService with AppName {
  val metrics = Metrics
  val auditConnector: AuditConnector = MicroserviceAuditConnector
}

trait AuditService {
  this: AppName =>
  val auditConnector: AuditConnector
  val metrics: Metrics

  val logMessageFormat = (controller: String, controllerAction: String, tavcReferenceNumber: String, statusCode: String, message: String) =>
    s"[$controller] [$controllerAction] [$tavcReferenceNumber] [$statusCode] - $message"


  def sendTAVCAdvancedAssuranceEvent(submittedDataForAudit: AASubmissionDataForAuditModel, tavcReferenceNumber: String,
                                           responseReceived: HttpResponse, acknowledgementRef: String)
                                          (implicit hc: HeaderCarrier, rh: RequestHeader): Unit = {

    val failureReason: String = if (checkResponseSuccess(responseReceived.status)) AAAuditConstants.notApplicable
    else
      getResponseReason(responseReceived).fold(AAAuditConstants.noValueProvided)(_.toString)

    val np = AAAuditConstants.noValueProvided


    val detailData = AASubmissionAuditDetail(
      statusCode = responseReceived.status.toString,
      failureReason = failureReason,
      tavcReferenceNumber = tavcReferenceNumber,
      acknowledgementReference = acknowledgementRef,
      companyName = submittedDataForAudit.companyName.fold(AAAuditConstants.noValueProvided)(_.toString),
      proposedInvestmentAmount = submittedDataForAudit.proposedInvestmentAmount.fold(AAAuditConstants.noValueProvided)(_.toString),
      forename = submittedDataForAudit.forename.fold(AAAuditConstants.noValueProvided)(_.toString),
      surname = submittedDataForAudit.surname.fold(AAAuditConstants.noValueProvided)(_.toString),
      phoneNumber = submittedDataForAudit.phoneNumber.fold(AAAuditConstants.noValueProvided)(_.toString),
      mobileNumber = submittedDataForAudit.mobileNumber.fold(AAAuditConstants.noValueProvided)(_.toString),
      emailAddress = submittedDataForAudit.emailAddress.fold(AAAuditConstants.noValueProvided)(_.toString),
      schemeTypes = submittedDataForAudit.schemeTypes,
      registeredAddress = submittedDataForAudit.registeredAddress,
      contactAddress = submittedDataForAudit.contactAddress)

    auditConnector.sendEvent(ExtendedDataEvent.apply(appName, AAAuditConstants.submitAuditType,
      tags = hc.toAuditTags(AAAuditConstants.transactionName, rh.path),
      detail = Json.toJson(detailData)))


  }

  def sendTAVCSubmitComplianceStatementEvent(submittedDataForAudit: CSSubmissionDataForAuditModel, tavcReferenceNumber: String,
                                           responseReceived: HttpResponse, acknowledgementRef: String)
                                          (implicit hc: HeaderCarrier, rh: RequestHeader): Unit = {

    val failureReason: String = if (checkResponseSuccess(responseReceived.status)) AAAuditConstants.notApplicable
    else
      getResponseReason(responseReceived).fold(AAAuditConstants.noValueProvided)(_.toString)

    val np = AAAuditConstants.noValueProvided

    val detailData = CSSubmissionAuditDetail(
      statusCode = responseReceived.status.toString,
      failureReason = failureReason,
      tavcReferenceNumber = tavcReferenceNumber,
      acknowledgementReference = acknowledgementRef,
      companyName = submittedDataForAudit.companyName.fold(AAAuditConstants.noValueProvided)(_.toString),
      totalAmountRaised = submittedDataForAudit.totalAmountRaised.fold(CSAuditConstants.noValueProvided)(_.toString),
      forename = submittedDataForAudit.forename.fold(CSAuditConstants.noValueProvided)(_.toString),
      surname = submittedDataForAudit.surname.fold(CSAuditConstants.noValueProvided)(_.toString),
      phoneNumber = submittedDataForAudit.phoneNumber.fold(CSAuditConstants.noValueProvided)(_.toString),
      mobileNumber = submittedDataForAudit.mobileNumber.fold(CSAuditConstants.noValueProvided)(_.toString),
      emailAddress = submittedDataForAudit.emailAddress.fold(CSAuditConstants.noValueProvided)(_.toString),
      schemeType = submittedDataForAudit.schemeType.fold(CSAuditConstants.noValueProvided)(_.toString),
      registeredAddress = submittedDataForAudit.registeredAddress,
      contactAddress = submittedDataForAudit.contactAddress)

    auditConnector.sendEvent(ExtendedDataEvent.apply(appName, CSAuditConstants.submitAuditType,
      tags = hc.toAuditTags(CSAuditConstants.transactionName, rh.path),
      detail = Json.toJson(detailData)))
  }

  def logSubscriptionResponseAA(responseReceived: HttpResponse, controller: String, controllerAction: String,
                                safeId: String): String = {
    logSubscriptionResponse(responseReceived, controller, controllerAction, safeId,  MetricsEnum.TAVC_SUBMISSION)
  }

  def logSubscriptionResponseCS(responseReceived: HttpResponse, controller: String, controllerAction: String,
                                safeId: String): String = {
    logSubscriptionResponse(responseReceived, controller, controllerAction, safeId,  MetricsEnum.TAVC_SUBMISSION_CS)
  }

  private def logSubscriptionResponse(responseReceived: HttpResponse, controller: String, controllerAction: String,
                              safeId: String, enum: MetricsEnum.Value): String = {

    val statusCode = responseReceived.status
    val message = getResponseReason(responseReceived).fold(getDefaultStatusMessage(statusCode))(_.toString)
    val logMessage = logMessageFormat(controller, controllerAction, safeId, statusCode.toString, message)

    checkResponseSuccess(responseReceived.status) match {
      case true =>
        metrics.incrementSuccessCounter(enum)
        Logger.info(logMessage)
      case _ =>
        metrics.incrementFailedCounter(enum)
        Logger.warn(logMessage)
    }

    logMessage
  }



  private def getResponseReason(response: HttpResponse): Option[String] = {
    Try {
      if (response.body.nonEmpty && response.body.contains("reason"))
        Some((response.json \ "reason").as[String])
      else None
    } match {
      case Success(result) => result
      case Failure(_) => None
    }
  }

  private def checkResponseSuccess(statusCode: Int): Boolean = {
    statusCode.toString.startsWith("2")
  }

  private def getDefaultStatusMessage(statusCode: Int): String = {
    if (checkResponseSuccess(statusCode))
      ResponseConstants.success
    else {
      statusCode match {
        case Status.NOT_FOUND => ResponseConstants.defaultNotFound
        case Status.BAD_REQUEST => ResponseConstants.defaultBadRequest
        case Status.SERVICE_UNAVAILABLE => ResponseConstants.defaultServiceUnavailable
        case Status.INTERNAL_SERVER_ERROR => ResponseConstants.defaultInternalServerError
        case _ => ResponseConstants.defaultOther
      }
    }
  }

}
