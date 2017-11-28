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
import fixtures.InvestorFixture
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.api.Play.current

class CrossFieldValidationHelperSpec extends UnitSpec with WithFakeApplication with InvestorFixture{

  "Calling validate accross fields" when {
    "using country code GB, a postcode and two complete UnitIssues" should {
      "return true for individual investors" in {
        CrossFieldValidationHelper.validateAcrossFields(0,validIndividualInvestor).size shouldBe 0
      }
      "return true for company investors" in {
        CrossFieldValidationHelper.validateAcrossFields(0,validCompanyInvestor).size shouldBe 0
      }
    }
    "an individual with any company specific fields filled out" should {
      "return false for hasIndividualEmptyFields" in{
        val errors = CrossFieldValidationHelper.validateAcrossFields(0,individualWithCompanyDetails)
        errors.size shouldBe 1
        errors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasIndividualEmptyFields")
      }
    }
    "a company with any individual specific fields filled out" should {
      "return false for hasCompanyEmptyFields" in{
        val errors = CrossFieldValidationHelper.validateAcrossFields(0,companyWithIndividualDetails)
        errors.size shouldBe 1
        errors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasCompanyEmptyFields")
      }
    }
    "an individual without individual specific fields filled out" should {
      "return false for hasIndividualRequiredFields" in{
        val errors = CrossFieldValidationHelper.validateAcrossFields(0,individualWithoutDetails)
        errors.size shouldBe 1
        errors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasIndividualRequiredFields")
      }
    }
    "a company without company specific fields filled out" should {
      "return false for hasCompanyRequiredFields" in{
        val errors = CrossFieldValidationHelper.validateAcrossFields(0,companyWithoutDetails)
        errors.size shouldBe 1
        errors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasCompanyRequiredFields")
      }
    }
    "using a country code that isn't GB and no postcode" should {
      "return true" in {
        CrossFieldValidationHelper.validateAcrossFields(0,validIndividualInvestorNonGB).size shouldBe 0
        CrossFieldValidationHelper.validateAcrossFields(0,validCompanyInvestorNonGB).size shouldBe 0
      }
    }
    "using country code GB and no postcode" should {
      "return false" in {
        val individualErrors = CrossFieldValidationHelper.validateAcrossFields(0,invalidIndividualInvestorPostcode)
        individualErrors.size shouldBe 1
        individualErrors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasCountryCodeRequiredFields")
        val companyErrors = CrossFieldValidationHelper.validateAcrossFields(0,invalidCompanyInvestorPostcode)
        companyErrors.size shouldBe 1
        companyErrors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasCountryCodeRequiredFields")
      }
    }
    "any of the unit issues are incomplete" should {
      "return false regardless of which field was empty, or how many unitIssues there are" in {
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueOneInvestor).size shouldBe 1
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueOneInvestor)(0) shouldBe "Row 0, " +
          Messages("cross.field.validation.hasUnitIssuesRequiredFields")
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueTwoInvestor).size shouldBe 1
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueTwoInvestor)(0) shouldBe "Row 0, " +
          Messages("cross.field.validation.hasUnitIssuesRequiredFields")
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueThreeInvestor).size shouldBe 1
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueThreeInvestor)(0) shouldBe "Row 0, " +
          Messages("cross.field.validation.hasUnitIssuesRequiredFields")
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueFourInvestor).size shouldBe 1
        CrossFieldValidationHelper.validateAcrossFields(0,invalidUnitIssueFourInvestor)(0) shouldBe "Row 0, " +
          Messages("cross.field.validation.hasUnitIssuesRequiredFields")
      }
    }
    "there are multiple errors with individuals" should {
      "return every error" in{
        val errors = CrossFieldValidationHelper.validateAcrossFields(0,individualWithMutlipleErrors)
        errors.size shouldBe 4
        errors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasIndividualEmptyFields")
        errors(1) shouldBe "Row 0, " + Messages("cross.field.validation.hasIndividualRequiredFields")
        errors(2) shouldBe "Row 0, " + Messages("cross.field.validation.hasCountryCodeRequiredFields")
        errors(3) shouldBe "Row 0, " + Messages("cross.field.validation.hasUnitIssuesRequiredFields")
      }
    }
    "there are multiple errors with a company" should {
      "return every error" in{
        val errors = CrossFieldValidationHelper.validateAcrossFields(0,companyWithMutlipleErrors)
        errors.size shouldBe 3
        errors(0) shouldBe "Row 0, " + Messages("cross.field.validation.hasCompanyEmptyFields")
        errors(1) shouldBe "Row 0, " + Messages("cross.field.validation.hasCompanyRequiredFields")
        errors(2) shouldBe "Row 0, " + Messages("cross.field.validation.hasUnitIssuesRequiredFields")
      }
    }
  }

}
