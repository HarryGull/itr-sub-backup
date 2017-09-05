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

package helpers

import common.AuditConstants
import model._
import models.{CSSubmissionDataForAuditModel, AuditAddressModel, SchemeTypesModel, AASubmissionDataForAuditModel}
import org.mockito.ArgumentCaptor
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.play.audit.model.{Audit, DataEvent, ExtendedDataEvent}
import uk.gov.hmrc.play.http.HttpResponse

object AuditHelper extends AuditHelper

trait AuditHelper {


  val testRequestPath = "test/path"
  def responseReasonContent(code: Int, message: String) : String = {
    s"""POST of 'http://localhost:9639/tax-assured-venture-capital/taxpayers/XA0000019191/returns'
       |returned {$code}. Response body: '{\"reason\":\"$message}\"}'""".
      stripMargin
    }

  val responseSuccessContent = s"""{"processingDate":"2014-12-17T09:30:47Z","formBundleNumber":"FBUND98763284"}"""

  // logging
  val reasonMessage = (message: String) => s"""{"message" : "$message"}"""
  //val eventCaptor2 = ArgumentCaptor.forClass(classOf[DataEvent])
  val eventCaptor = ArgumentCaptor.forClass(classOf[Audit])

  val responseNonContent = HttpResponse(NO_CONTENT)
  val responseBadRequestNoContent = HttpResponse(BAD_REQUEST)
  val responseNotFoundNoContent = HttpResponse(NOT_FOUND)
  val responseServiceUnavailableNoContent = HttpResponse(SERVICE_UNAVAILABLE)
  val responseInternalServerErrorNoContent = HttpResponse(INTERNAL_SERVER_ERROR)
  val responseOtherErrorNoContent = HttpResponse(GATEWAY_TIMEOUT)
  val responseOkSuccess = HttpResponse(OK, Some(Json.parse(responseSuccessContent)))
  val responseOkNocontent = HttpResponse(OK)
  val responseCreatedNocontent = HttpResponse(CREATED)
  val responseBadRequestEtmpDuplicate = HttpResponse(BAD_REQUEST,
    Some(Json.toJson(responseReasonContent(BAD_REQUEST, EtmpResponseReasons.duplicateSubmission400))))
  val responseServiceUnavailableEtmpNotProcessed = HttpResponse(SERVICE_UNAVAILABLE,
    Some(Json.toJson(responseReasonContent(SERVICE_UNAVAILABLE,EtmpResponseReasons.notProcessed503))))
  val responseInternalServerErrorEtmpSap = HttpResponse(INTERNAL_SERVER_ERROR,
    Some(Json.toJson(responseReasonContent(INTERNAL_SERVER_ERROR,EtmpResponseReasons.sapError500))))
  val responseInternalServerErrorEtmpRegime = HttpResponse(INTERNAL_SERVER_ERROR,
    Some(Json.toJson(responseReasonContent(INTERNAL_SERVER_ERROR, EtmpResponseReasons.noRegime500))))
  val responseInternalServerErrorEtmp = HttpResponse(INTERNAL_SERVER_ERROR,
    Some(Json.toJson(responseReasonContent(INTERNAL_SERVER_ERROR, EtmpResponseReasons.serverError500))))
  val submissionControllerTestName = "SubmissionController"
  val subscriptionControllerTestName = "SubscriptionController"
  val subscribeTestAction = "subscribe"
  val submitTestAction = "submit"

  val safeId = "XA0001234567890"
  val tavcRefNumber = "XLTAVC000823190"
  val acknowledgementReference = "XE00012345678901477052976"

  val fullAddress = AuditAddressModel(
    addressLine1 = Some("Line 1"),
    addressLine2 = Some("Line 2"),
    addressLine3 = Some("Line 3"),
    addressLine4 = Some("Line 4"),
    postCode = Some("AA1 1AA"),
    country = Some("GB")
  )

  val fullAuditDataAA=  AASubmissionDataForAuditModel(companyName = Some("Test ltd."), proposedInvestmentAmount = Some("250"),
    forename = Some("nameOne"), surname = Some("nameTwo"),phoneNumber = Some("000001 100000"),
    mobileNumber = Some("000002 200000"),emailAddress = Some("test@test.com"),
    schemeTypes = SchemeTypesModel(true, true, false, true), contactAddress = Some(fullAddress),
    registeredAddress = Some(fullAddress))

  val fullAuditDataCS=  CSSubmissionDataForAuditModel(companyName = Some("Test ltd."), totalAmountRaised = Some("250"),
    forename = Some("nameOne"), surname = Some("nameTwo"),phoneNumber = Some("000001 100000"),
    mobileNumber = Some("000002 200000"),emailAddress = Some("test@test.com"),
    schemeType = Some("SEIS"), contactAddress = Some(fullAddress),
    registeredAddress = Some(fullAddress))
}
