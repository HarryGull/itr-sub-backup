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

package models

import java.awt.font.FontRenderContext

import common.AuditConstants
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class SubmissionAuditDetail(
                                      statusCode: String,
                                      failureReason: String,
                                      tavcReferenceNumber: String,
                                      acknowledgementReference: String,
                                      forename: String,
                                      surname: String,
                                      phoneNumber: String,
                                      mobileNumber: String,
                                      emailAddress: String,
                                      contactAddress: Option[AuditAddressModel],
                                      registeredAddress: Option[AuditAddressModel]
                                )


object SubmissionAuditDetail {
  implicit val auditSubmissionWrites = Json.format[SubmissionAuditDetail]

  //  implicit val readsAuditAddress: Reads[SubmissionAuditDetail] = (
  //    (__ \ "submissionType" \ "correspondenceDetails" \ "contactName" \ "emailAddress").readNullable[String] and
  //      (__ \ "addressLine2").readNullable[String] and
  //      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "emailAddress").readNullable[String] and
  //      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "emailAddress").readNullable[String] and
  //      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "emailAddress").readNullable[String] and
  //      (__ \ "addressLine4").readNullable[String] and
  //      (__ \ "postalCode").readNullable[String] and
  //      (__ \ "countryCode").readNullable[String]
  //    ) (SubmissionAuditDetail.apply _)
}
