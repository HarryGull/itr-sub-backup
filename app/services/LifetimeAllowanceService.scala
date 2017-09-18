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

package services

object LifetimeAllowanceService extends LifetimeAllowanceService{
  val maxKi: Int = 20000000
  val maxNonKi: Int = 12000000
}

trait LifetimeAllowanceService{

  val maxKi: Int
  val maxNonKi: Int

  def checkLifetimeAllowanceExceeded(hadPrevRFI: Boolean, isKi: Boolean, previousInvestmentSchemesTotal: Long, proposedAmount: Long): Boolean = {

    /** Checks that all operating costs are greater than zero. 'forall' short hand for map and contains **/
    def validAmounts: Boolean = if(hadPrevRFI)previousInvestmentSchemesTotal > 0 && proposedAmount > 0 else proposedAmount > 0

    def checkTotalKI(hadPreviousRFI: Boolean, previousInvestmentSchemesTotal: Long, proposedAmount: Long) : Boolean =  {
      if(hadPreviousRFI) proposedAmount + previousInvestmentSchemesTotal > maxKi
      else proposedAmount > maxKi
    }

    def checkTotalNonKI(hadPreviousRFI: Boolean, previousInvestmentSchemesTotal: Long, proposedAmount: Long) : Boolean = {
      if(hadPreviousRFI) proposedAmount + previousInvestmentSchemesTotal > maxNonKi
      else proposedAmount > maxNonKi
    }

    if(validAmounts){
      if(isKi)
        checkTotalKI(hadPrevRFI, previousInvestmentSchemesTotal, proposedAmount)
      else
        checkTotalNonKI(hadPrevRFI, previousInvestmentSchemesTotal, proposedAmount)
    } else false
  }
}
