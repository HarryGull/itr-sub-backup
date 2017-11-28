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
import common.Constants
import models.submission._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class CrossFieldValidationHelperSpec extends UnitSpec with WithFakeApplication{

  val postalCodeSome = Some("AA1 1AA")
  val countryCodeGB = Constants.countryCodeGB
  val countryCodeOther = "US"

  val validIndividualDetails = Some(x = DesIndividualDetailsModel(DesContactName("name1", "name2"),
    DesAddressType("addressLine1", "addressLine2", None, None, postalCodeSome, countryCodeGB)))
  val invalidIndividualPostcode = Some(DesIndividualDetailsModel(DesContactName("name1","name2"),
    DesAddressType("addressLine1","addressLine2",None,None,None,countryCodeGB)))

  val validCompanyDetails = Some(DesCompanyDetailsModel("organisationName",None,None,
    Some(DesAddressType("addressLine1","addressLine2",None,None,postalCodeSome,countryCodeGB))))


  val holdingOne = UnitIssueModel("The 100's","01/01/2001","Shares",CostModel("5"),BigDecimal(3),CostModel("7"))
  val holdingTwo = UnitIssueModel("Preference shares type B Issue 1","02/03/20161","Shares",CostModel("2"),BigDecimal(3),CostModel("3"))




  val validIndividualInvestor = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(validIndividualDetails,None),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](holdingOne,holdingTwo)))))

  val invalidIndividualInvestor = DesInvestorModel("Individual",DesInvestorInfoModel(DesCompanyOrIndividualDetailsModel(invalidIndividualPostcode,None),
    BigDecimal(2),CostModel("10"),Some(DesGroupHoldingsModel(Seq[UnitIssueModel](holdingOne,holdingTwo)))))

  "Calling validateAcrossFields" should {
    "return an empty list of errors when a valid InvestorModel is passed" in {
      val errorList = CrossFieldValidationHelper.validateAcrossFields(0, invalidIndividualInvestor)
      errorList.size shouldBe 1
      errorList(0) shouldBe "test"
    }
  }

}
