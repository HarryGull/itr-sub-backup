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

package models.submission

import models._
import play.api.libs.json.Json

case class SubmitMarketInfoModel(
                             newGeographicalMarketModel: NewGeographicalMarketModel,
                             newProductModel: NewProductModel,
                             marketDescription: Option[String] = None
                           )

case class SubsidiaryPerformingTradeModel(
                                          ninetyOwnedModel: SubsidiariesNinetyOwnedModel,
                                          organisationName: String,
                                          ctUtr: Option[String] = None,
                                          crn: Option[String] = None,
                                          companyAddress: Option[AddressModel] = None
                                    )

case class OrganisationDetailsModel(
                                organisationName: String,
                                utr: Option[String] = None,
                                chrn: Option[String] = None,
                                startDate: DateOfIncorporationModel,
                                firstDateOfCommercialSale: Option[String] = None,
                                ctUtr: Option[String] = None,
                                crn: Option[String] = None,
                                companyAddress: Option[AddressModel] = None,
                                previousRFIs: Option[Seq[PreviousSchemeModel]]
                              )

case class AdvancedAssuranceSubmissionType(

                                            // minimum mandatory types required
                                            natureOfBusinessModel: NatureOfBusinessModel, // trade: baDescription
                                            contactDetailsModel: ContactDetailsModel,
                                            proposedInvestmentModel: ProposedInvestmentModel,
                                            investmentGrowModel: InvestmentGrowModel,
                                            organisationDetails: OrganisationDetailsModel,

                                            // mandatory types either statically generated or
                                            // hard-coded until we capture the details:
                                            organisationType: String = "Limited",

                                            // TODO: Contact address is mandatory but not fully captured yet so hard
                                            // coded below: remove this test address once read from ETMP detais
                                            correspondenceAddress: AddressModel,
                                            schemeTypes: SchemeTypesModel,
                                            // optional types
                                            whatWillUseForModel: Option[WhatWillUseForModel],
                                            marketInfo: Option[SubmitMarketInfoModel],
                                            annualCosts: Option[Seq[AnnualCostModel]],
                                            annualTurnover: Option[Seq[TurnoverCostModel]],
                                            acknowledgementReference: Option[String] = None,
                                            agentReferenceNumber: Option[String],
                                            knowledgeIntensive: Option[KiModel],
                                            subsidiaryPerformingTrade: Option[SubsidiaryPerformingTradeModel]
                                          )
case class Submission(
                       submission : AdvancedAssuranceSubmissionType
                     )

object Submission {
  implicit val formatOrgDetails = Json.format[OrganisationDetailsModel]
  implicit val formatSubsidiaryPerfomingTrade = Json.format[SubsidiaryPerformingTradeModel]
  implicit val formatSubmiKitModel = Json.format[KiModel]
  implicit val formatSubmitCostModel = Json.format[CostModel]
  implicit val formatSubmitAnnualCostModel = Json.format[AnnualCostModel]
  implicit val formatSubmitTurnoverCostModel = Json.format[TurnoverCostModel]
  implicit val formatSubmitMarketInfo = Json.format[SubmitMarketInfoModel]
  implicit val formatSubSubmissionType = Json.format[AdvancedAssuranceSubmissionType]
  implicit val formatSubSubmission = Json.format[Submission]
}
