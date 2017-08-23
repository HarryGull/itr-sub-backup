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

import common.{AAAuditConstants, CSAuditConstants}
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class AASubmissionDataForAuditModel(
                            companyName: Option[String] = Some(AAAuditConstants.noValueProvided),
                            proposedInvestmentAmount: Option[String] = Some(AAAuditConstants.noValueProvided),
                            forename: Option[String] = Some(AAAuditConstants.noValueProvided),
                            surname: Option[String] = Some(AAAuditConstants.noValueProvided),
                            phoneNumber:Option[String] = Some(AAAuditConstants.noValueProvided),
                            mobileNumber: Option[String] = Some(AAAuditConstants.noValueProvided),
                            emailAddress: Option[String] = Some(AAAuditConstants.noValueProvided),
                            schemeTypes: SchemeTypesModel,
                            contactAddress: Option[AuditAddressModel] = Some(AuditAddressModel()),
                            registeredAddress: Option[AuditAddressModel] = Some(AuditAddressModel())
                            )


object AASubmissionDataForAuditModel {
  implicit val submissionAuditDataWrites = Json.writes[AASubmissionDataForAuditModel]

  implicit val submissionAuditDataReads: Reads[AASubmissionDataForAuditModel] = (
    (__ \ "submissionType" \ "submission" \ "advancedAssurance" \ "organisation" \ "orgDetails" \"organisationName").readNullable[String] and
    (__ \ "submissionType" \ "submission" \ "advancedAssurance" \ "proposedInvestment" \ "investmentAmount" \"amount").readNullable[String] and
    (__ \ "submissionType" \ "correspondenceDetails" \ "contactName" \ "name1").readNullable[String] and
    (__ \ "submissionType" \ "correspondenceDetails" \ "contactName" \ "name2").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "phoneNumber").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "mobileNumber").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "emailAddress").readNullable[String] and
      (__ \ "submissionType" \ "submission" \ "advancedAssurance" \ "schemeTypes").read[SchemeTypesModel] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactAddress").readNullable[AuditAddressModel] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactAddress").readNullable[AuditAddressModel]
    ) (AASubmissionDataForAuditModel.apply _)
}


case class CSSubmissionDataForAuditModel(
                                          companyName: Option[String] = Some(CSAuditConstants.noValueProvided),
                                          totalAmountRaised: Option[String] = Some(CSAuditConstants.noValueProvided),
                                          forename: Option[String] = Some(CSAuditConstants.noValueProvided),
                                          surname: Option[String] = Some(CSAuditConstants.noValueProvided),
                                          phoneNumber:Option[String] = Some(CSAuditConstants.noValueProvided),
                                          mobileNumber: Option[String] = Some(CSAuditConstants.noValueProvided),
                                          emailAddress: Option[String] = Some(CSAuditConstants.noValueProvided),
                                          schemeType: Option[String] = Some(CSAuditConstants.noValueProvided),
                                          contactAddress: Option[AuditAddressModel] = Some(AuditAddressModel()),
                                          registeredAddress: Option[AuditAddressModel] = Some(AuditAddressModel())
                                      )


object CSSubmissionDataForAuditModel {
  implicit val submissionAuditDataWrites = Json.writes[CSSubmissionDataForAuditModel]

  implicit val submissionAuditDataReads: Reads[CSSubmissionDataForAuditModel] = (
    (__ \ "submissionType" \ "submission" \ "complianceStatement" \ "organisation" \ "orgDetails" \"organisationName").readNullable[String] and
      (__ \ "submissionType" \ "submission" \ "complianceStatement" \ "investment" \  "unitIssue" \"totalAmount" \"amount").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactName" \ "name1").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactName" \ "name2").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "phoneNumber").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "mobileNumber").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactDetails" \ "emailAddress").readNullable[String] and
      (__ \ "submissionType" \ "submission" \ "complianceStatement" \ "schemeType").readNullable[String] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactAddress").readNullable[AuditAddressModel] and
      (__ \ "submissionType" \ "correspondenceDetails" \ "contactAddress").readNullable[AuditAddressModel]
    ) (CSSubmissionDataForAuditModel.apply _)
}
