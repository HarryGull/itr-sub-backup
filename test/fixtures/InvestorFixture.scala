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

package fixtures

import common.Constants
import models.submission._

trait InvestorFixture {

  val unitIssueOne = UnitIssueModel("The 100's","01/01/2001","Shares",CostModel("5"),BigDecimal(3),CostModel("7"))
  val unitIssueTwo = UnitIssueModel("Preference shares type B Issue 1","02/03/20161","Shares",CostModel("2"),BigDecimal(3),CostModel("3"))
  val invalidUnitIssueOne = UnitIssueModel("","02/03/20161","Shares",CostModel("2"),BigDecimal(3),CostModel("3"))
  val invalidUnitIssueTwo = UnitIssueModel("Preference shares type B Issue 1","","Shares",CostModel("2"),BigDecimal(3),CostModel("3"))
  val invalidUnitIssueThree = UnitIssueModel("Preference shares type B Issue 1","02/03/20161","",CostModel("2"),BigDecimal(3),CostModel("3"))
  val invalidUnitIssueFour = UnitIssueModel("Preference shares type B Issue 1","02/03/20161","Shares",CostModel("2"),BigDecimal(0),CostModel("3"))

  val validIndividualInvestor = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,
      Some("AA1 1AA"), Constants.countryCodeGB))), None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val validIndividualInvestorNonGB = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,None, "US"))),
    None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val invalidIndividualInvestorPostcode = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,None, Constants.countryCodeGB))),
    None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val individualWithCompanyDetails = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,Some("AA1 1AA"),
      Constants.countryCodeGB))),Some(DesCompanyDetailsModel("organisationName",None,None,Some(DesAddressType("addressLine1","addressLine2",
      None,None,Some("AA1 1AA"), Constants.countryCodeGB))))),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val individualWithoutDetails = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(None,None),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))

  val validCompanyInvestor = DesInvestorModel("Company",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    None,Some(DesCompanyDetailsModel("organisationName",None,None,Some(DesAddressType("addressLine1","addressLine2",None,None,Some("AA1 1AA"), Constants.countryCodeGB))))),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val validCompanyInvestorNonGB = DesInvestorModel("Company",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    None,Some(DesCompanyDetailsModel("organisationName",None,None,Some(DesAddressType("addressLine1","addressLine2",None,None,None, "US"))))),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val invalidCompanyInvestorPostcode = DesInvestorModel("Company",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    None,Some(DesCompanyDetailsModel("organisationName",None,None,Some(DesAddressType("addressLine1","addressLine2",None,None,None, Constants.countryCodeGB))))),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val companyWithIndividualDetails = DesInvestorModel("Company",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,Some("AA1 1AA"),
      Constants.countryCodeGB))),Some(DesCompanyDetailsModel("organisationName",None,None,Some(DesAddressType("addressLine1","addressLine2",
      None,None,Some("AA1 1AA"), Constants.countryCodeGB))))), BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))
  val companyWithoutDetails = DesInvestorModel("Company",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(None,None),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](unitIssueOne,unitIssueTwo)))))

  val invalidUnitIssueOneInvestor = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,
      Some("AA1 1AA"), Constants.countryCodeGB))),None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](invalidUnitIssueOne)))))
  val invalidUnitIssueTwoInvestor = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,Some("AA1 1AA"),
      Constants.countryCodeGB))),None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](invalidUnitIssueTwo,unitIssueOne)))))
  val invalidUnitIssueThreeInvestor = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,Some("AA1 1AA"),
      Constants.countryCodeGB))),None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](invalidUnitIssueThree,unitIssueOne,unitIssueTwo)))))
  val invalidUnitIssueFourInvestor = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,Some("AA1 1AA"),
      Constants.countryCodeGB))),None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](invalidUnitIssueFour)))))

  val individualWithMutlipleErrors = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    None,Some(DesCompanyDetailsModel("organisationName",None,None,Some(DesAddressType("addressLine1","addressLine2",None,None,None, Constants.countryCodeGB))))),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](invalidUnitIssueOne)))))
  val companyWithMutlipleErrors = DesInvestorModel("Company",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(
    Some(DesIndividualDetailsModel(DesContactName("name1","name2"),DesAddressType("addressLine1","addressLine2",None,None,Some("AA1 1AA"),
      Constants.countryCodeGB))),None),BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](invalidUnitIssueOne,invalidUnitIssueTwo)))))




}
