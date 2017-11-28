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
import models.submission.{DesInvestorModel, UnitIssueModel}
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Play.current

object CrossFieldValidationHelper {

  def validateAcrossFields(row: Int, investor: DesInvestorModel): List[String] = {

    def predicateResponse(predicate: Boolean, message: => String) = if (predicate) None else Some(s"Row $row, $message")

    /** Where Column 2 is "Company" then Columns 3 & 4 must be blank **/
    val hasCompanyEmptyFields: Boolean =
      if (investor.investorType == Constants.typeCompany) investor.investorInfo.investorDetails.individualDetails.isEmpty else true


    /** Where Column 2 is "Company" then Column 5 cannot be blank **/
    val hasCompanyRequiredFields: Boolean =
      if (investor.investorType == Constants.typeCompany) investor.investorInfo.investorDetails.companyDetails.isDefined else true

    /** Where Column 2 is "Individual" then Column 5 must be blank **/
    val hasIndividualEmptyFields: Boolean = {
      if (investor.investorType == Constants.typeIndividual) investor.investorInfo.investorDetails.companyDetails.isEmpty else true
    }

    /** Where Column 2 is "Individual" then Column 3 cannot be blank **/
    val hasIndividualRequiredFields: Boolean =
      if (investor.investorType == Constants.typeIndividual) investor.investorInfo.investorDetails.individualDetails.isDefined else true

    /** Where Column 11 is "GB" then Column 10 cannot be blank **/
    val hasCountryCodeRequiredFields: Boolean = {
      if (investor.investorInfo.investorDetails.individualDetails.isDefined
        && investor.investorInfo.investorDetails.individualDetails.get.individualAddress.countryCode == Constants.countryCodeGB){
        investor.investorInfo.investorDetails.individualDetails.get.individualAddress.postalCode.isDefined
      } else if (investor.investorInfo.investorDetails.companyDetails.isDefined
        && investor.investorInfo.investorDetails.companyDetails.get.companyAddress.get.countryCode == Constants.countryCodeGB) {
        investor.investorInfo.investorDetails.companyDetails.get.companyAddress.get.postalCode.isDefined
      }
      else true
    }

    /** For each block of four columns from Column   24, if one entry is populated all
      * four data items must all be.populated Note: this must be able to handle multiple existing shareholdings in the investor (24-27,28-31, ...) **/
    val hasUnitIssuesRequiredFields: Boolean = {
      def isValidUnit(unit: UnitIssueModel): Boolean =
        !(unit.description.isEmpty || unit.dateOfIssue.isEmpty || unit.unitType.isEmpty || unit.numberUnitsIssued == 0)

      investor.investorInfo.existingGroupHoldings.get.groupHolding.forall(isValidUnit)
    }

    List(
      predicateResponse(hasCompanyEmptyFields, Messages("cross.field.validation.hasCompanyEmptyFields")),
      predicateResponse(hasCompanyRequiredFields, Messages("cross.field.validation.hasCompanyRequiredFields")),
      predicateResponse(hasIndividualEmptyFields, Messages("cross.field.validation.hasIndividualEmptyFields")),
      predicateResponse(hasIndividualRequiredFields, Messages("cross.field.validation.hasIndividualRequiredFields")),
      predicateResponse(hasCountryCodeRequiredFields, Messages("cross.field.validation.hasCountryCodeRequiredFields")),
      predicateResponse(hasUnitIssuesRequiredFields, Messages("cross.field.validation.hasUnitIssuesRequiredFields"))).flatten
  }

}
