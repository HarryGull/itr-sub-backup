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

package services

import scala.concurrent.Future

object LifetimeAllowanceService extends LifetimeAllowanceService{
  val maxKi: Int = 20000000
  val maxNonKi: Int = 12000000
}

trait LifetimeAllowanceService{

  val maxKi: Int
  val maxNonKi: Int

  def checkLifetimeAllowanceExceeded(isKi: Boolean, previousInvestmentSchemesTotal: Int, proposedAmount: Int): Boolean = {

    /** Checks that all operating costs are greater than zero. 'forall' short hand for map and contains **/
    def validAmounts: Boolean = previousInvestmentSchemesTotal > 0 && proposedAmount > 0

    if(validAmounts){
      if (isKi) proposedAmount + previousInvestmentSchemesTotal > maxKi
      else proposedAmount + previousInvestmentSchemesTotal > maxNonKi
    } else false
  }
}
