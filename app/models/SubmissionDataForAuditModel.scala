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

package models

import common.AuditConstants
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class SubmissionDataForAuditModel(
                            companyName: Option[String] = Some(AuditConstants.noValueProvided),
                            proposedInvestmentAmount: Option[String] = Some(AuditConstants.noValueProvided),
                            forename: Option[String] = Some(AuditConstants.noValueProvided),
                            surname: Option[String] = Some(AuditConstants.noValueProvided),
                            phoneNumber:Option[String] = Some(AuditConstants.noValueProvided),
                            mobileNumber: Option[String] = Some(AuditConstants.noValueProvided),
                            emailAddress: Option[String] = Some(AuditConstants.noValueProvided),
                            contactAddress: Option[AuditAddressModel] = Some(AuditAddressModel()),
                            registeredAddress: Option[AuditAddressModel] = Some(AuditAddressModel())
                            )


object SubmissionDataForAuditModel {
  implicit val submissionAuditDataWrites = Json.writes[SubmissionDataForAuditModel]

  implicit val submissionAuditDataReads: Reads[SubmissionDataForAuditModel] = (
    (__ \ "submissionType" \ "submission" \ "advancedAssurance" \ "organisation" \ "orgDetails" \"organisationName").readNullable[String] and
    (__ \ "submissionType" \ "submission" \ "advancedAssurance" \ "proposedInvestment" \ "investmentAmount" \"amount").readNullable[String] and
    (__ \ "submissionType" \ "correspondenceDetails" \ "contactName" \ "name1").readNullable[String] and
    (__ \ "submissionType" \ "correspondenceDetails" \ "contactName" \ "name2").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "phoneNumber").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "mobileNumber").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "emailAddress").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactAddress").readNullable[AuditAddressModel] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactAddress").readNullable[AuditAddressModel]
    ) (SubmissionDataForAuditModel.apply _)
}
