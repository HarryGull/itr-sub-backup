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

import play.api.libs.json._

case class DesCostModel(
                      amount : String,
                      currency: String = "GBP"
                    )

case class ProposedAmount (
                            amount: String,
                            currency: String = "GBP"
)

case class SubmitDesContactName(
                              name1: String,
                              name2: String
                            )

case class SubmitDesMarketInfo(
                       newGeographicMarket: Boolean,
                       newProductMarket: Boolean,
                       marketDescription: Option[String]
                     )

case class SubmitDesContactDetails(
                                    phoneNumber: Option[String],
                                    mobileNumber: Option[String],
                                    faxNumber: Option[String],
                                    emailAddress: Option[String]
                               )

case class SubmitDesAddressType(
                                 addressLine1: String,
                                 addressLine2: String,
                                 addressLine3: Option[String],
                                 addressLine4: Option[String],
                                 postalCode: Option[String],
                                 countryCode: String
                               )

case class SubmitDesCompanyDetailsType(
                                    organisationName: String,
                                    ctUtr:Option[String],
                                    crn:Option[String],
                                    companyAddress: Option[SubmitDesAddressType]
                                  )

case class SubmitDesOrganisation(
                                  utr:Option[String],
                                  chrn:Option[String],
                                  startDate:String,
                                  firstDateOfCommercialSale:Option[String],
                                  orgDetails:SubmitDesCompanyDetailsType,
                                  previousRFIs: Option[RFICostsModel]
                                )

case class SubmitDesSubsidiaryPerformingTrade(
                                               ninetyPercentOwned: Boolean,
                                               companyDetails: SubmitDesCompanyDetailsType
                                             )

case class SubmitDesCorrespondenceDetails(
                                         contactName: SubmitDesContactName,
                                         contactDetails: SubmitDesContactDetails,
                                         contactAddress: SubmitDesAddressType
                                       )
case class AnnualTurnoversModel(
                                 annualTurnover: Seq[TurnoverCostModel]
                               )

case class AnnualCostsModel(
                             annualCost: Seq[AnnualCostModel]
                           )

case class DateFromModel(
                          issueDate: String
                        )

case class RFIModel(
                           schemeType: String,
                           name: Option[String],
                           issueDate: String,
                           amount: DesCostModel,
                           amountSpent: Option[DesCostModel]
                         )

case class RFICostsModel(
                             previousRFI: Seq[RFIModel]
                           )

case class SubmitDesTradeModel(

                              businessActivity: Option[String],
                              baDescription: String,
                              marketInfo: Option[SubmitDesMarketInfo],
                              dateTradeCommenced: String,
                              annualCosts: Option[AnnualCostsModel],
                              annualTurnover:  Option[AnnualTurnoversModel]
                            )

case class SubmitDesProposedInvestment(
                                        growthJustification: String,
                                        unitType: String,
                                        investmentAmount: ProposedAmount
                                      )

case class SubmitDesSubmission(
                                advancedAssurance: SubmitDesAdvancedAssurance
                              )

case class SubmitDesAdvancedAssurance(
                                        schemeTypes: SchemeTypesModel,
                                        trade: SubmitDesTradeModel,
                                        proposedInvestment: SubmitDesProposedInvestment,
                                        subsidiaryPerformingTrade: Option[SubmitDesSubsidiaryPerformingTrade],
                                        knowledgeIntensive: Option[KiModel],
                                        organisation: SubmitDesOrganisation
                                      )
case class SubmitDesSubmissionType(
                                 agentReferenceNumber: Option[String],
                                 correspondenceDetails: SubmitDesCorrespondenceDetails,
                                 organisationType: String,
                                 submission: SubmitDesSubmission
                               )

case class DesSubmitAdvancedAssuranceModel(
                                            acknowledgementReference: Option[String] = None,
                                            submissionType: SubmitDesSubmissionType
                               )

object DesSubmitAdvancedAssuranceModel {

  implicit val costModelFormat = Json.format[CostModel]
  implicit val kiModelFormat = Json.format[KiModel]
  implicit val desModelFormat = Json.format[DesCostModel]
  implicit val FormatCaFormat = Json.format[SubmitDesAddressType]
  implicit val companyDetailsTypeFormat = Json.format[SubmitDesCompanyDetailsType]
  implicit val companyPerformingTradeFormat = Json.format[SubmitDesSubsidiaryPerformingTrade]
  implicit val proposedAmountFormat = Json.format[ProposedAmount]
  implicit val investmentModelFormat = Json.format[SubmitDesProposedInvestment]
  implicit val annualCostModelFormat = Json.format[AnnualCostModel]
  implicit val rfiModelFormat = Json.format[RFIModel]
  implicit val turnoverCostModelFormat = Json.format[TurnoverCostModel]
  implicit val FormatCnFormat = Json.format[SubmitDesContactName]
  implicit val FormatMktInfoFormat = Json.format[SubmitDesMarketInfo]
  implicit val FormatCdFormat = Json.format[SubmitDesContactDetails]
  implicit val annualTurnoversModelFormat = Json.format[AnnualTurnoversModel]
  implicit val annualCostsModelFormat = Json.format[AnnualCostsModel]
  implicit val rfiCostsModelFormat = Json.format[RFICostsModel]
  implicit val FormatTradeModelFormat = Json.format[SubmitDesTradeModel]
  implicit val FormatCorDetailsFormat = Json.format[SubmitDesCorrespondenceDetails]
  implicit val organisationFormat = Json.format[SubmitDesOrganisation]
  implicit val FormatAAFormatFormat = Json.format[SubmitDesAdvancedAssurance]
  implicit val FormatAAgFormatFormat = Json.format[SubmitDesSubmission]
  implicit val FormatSubTypeFormat = Json.format[SubmitDesSubmissionType]
  implicit val DesfFullSubmitAAModelFormat = Json.format[DesSubmitAdvancedAssuranceModel]

}
