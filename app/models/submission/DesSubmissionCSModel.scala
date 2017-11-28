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

package models.submission


import play.api.libs.json.Json


case class DesIndividualDetailsModel(
                                      individualName: DesContactName,
                                      individualAddress: DesAddressType
                                    )
object DesIndividualDetailsModel{
  implicit val formats = Json.format[DesIndividualDetailsModel]
}
case class DesCompanyDetailsModel(
                                   organisationName: String,
                                   ctUtr:Option[String],
                                   crn:Option[String],
                                   companyAddress: Option[DesAddressType]
                                 )
object DesCompanyDetailsModel{
  implicit val formats = Json.format[DesCompanyDetailsModel]
}

case class DesCompanyOrIndividualDetailsModel(
                                               individualDetails: Option[DesIndividualDetailsModel],
                                               companyDetails: Option[DesCompanyDetailsModel]
                                             )
object DesCompanyOrIndividualDetailsModel{
  implicit val formats = Json.format[DesCompanyOrIndividualDetailsModel]
}

case class DesPreviousOwnershipModel(
                                      dateAcquired: String,
                                      prevOwnerStartDate: Option[String],
                                      previousOwner: DesCompanyOrIndividualDetailsModel
                                    )
object DesPreviousOwnershipModel{
  implicit val formats = Json.format[DesPreviousOwnershipModel]
}

case class DesTradeModel(
                          businessActivity: Option[String],
                          baDescription: String,
                          marketInfo: Option[DesMarketInfo], // eis only
                          thirtyDayRule: Option[Boolean],
                          dateTradeCommenced: String,
                          annualCosts: Option[DesAnnualCostsModel],
                          annualTurnover:  Option[DesAnnualTurnoversModel],
                          previousOwnership: Option[DesPreviousOwnershipModel]
                        )

object DesTradeModel{
  implicit val formats = Json.format[DesTradeModel]
}


case class DesOrganisationStatusModel(
                                       numberOfFTEmployees: BigDecimal,
                                       shareOrLoanCapitalChanges: String,
                                       grossAssetBefore: CostModel,
                                       grossAssetAfter: CostModel
                                     )
object DesOrganisationStatusModel{
  implicit val formats = Json.format[DesOrganisationStatusModel]
}

case class DesInvestmentDetailsModel(
                                      growthJustification: String, // required as per DES scheme but not in CS Flow
                                      unitIssue: UnitIssueModel,
                                      amountSpent: Option[CostModel],
                                      organisationStatus: Option[DesOrganisationStatusModel]
                                    )
object DesInvestmentDetailsModel{
  implicit val formats = Json.format[DesInvestmentDetailsModel]
}

case class DesSubsidiaryPerformingTrade(
                                         ninetyPercentOwned: Boolean,
                                         companyDetails: DesCompanyDetailsModel
                                       )
object DesSubsidiaryPerformingTrade{
  implicit val formats = Json.format[DesSubsidiaryPerformingTrade]
}
case class DesGroupHoldingsModel(
                                  groupHolding: Seq[UnitIssueModel]
                                )
object DesGroupHoldingsModel{
  implicit val formats = Json.format[DesGroupHoldingsModel]
}
case class DesInvestorInfoModel(
                                 investorDetails: DesCompanyOrIndividualDetailsModel,
                                 numberOfUnitsHeld: BigDecimal,
                                 investmentAmount: CostModel,
                                 existingGroupHoldings: Option[DesGroupHoldingsModel]
                               )
object DesInvestorInfoModel{
  implicit val formats = Json.format[DesInvestorInfoModel]
}

case class DesInvestorModel(
                             investorType: String,
                             investorInfo: DesInvestorInfoModel
                           )
object DesInvestorModel{
  implicit val formats = Json.format[DesInvestorModel]
}

case class DesInvestorDetailsModel(
                                    investor: Seq[DesInvestorModel]
                                  )
object DesInvestorDetailsModel{
  implicit val formats = Json.format[DesInvestorDetailsModel]
}
case class DesRepaymentModel(
                              repaymentDate: Option[String],
                              repaymentAmount: CostModel,
                              unitType: Option[String],
                              holdersName: Option[DesContactName],
                              subsidiaryName: Option[String]
                            )
object DesRepaymentModel{
  implicit val formats = Json.format[DesRepaymentModel]
}

case class DesRepaymentsModel(
                               repayment: Seq[DesRepaymentModel]
                             )
object DesRepaymentsModel{
  implicit val formats = Json.format[DesRepaymentsModel]
}