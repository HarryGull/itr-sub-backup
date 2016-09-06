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

package controllers

import services.LifetimeAllowanceService
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar

import uk.gov.hmrc.play.test.UnitSpec

class LifetimeAllowanceServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach{

  val isKi = true
  val notKi = false
  val previousInvestmentSchemesKiTotalOver = 20000000
  val previousInvestmentSchemesKiTotalBoundary = 19999999
  val previousInvestmentSchemesNotKiTotalOver = 12000000
  val previousInvestmentSchemesNotKiTotalBoundary = 11999999
  val proposedAmount = 1
  val emptyAmount = 0

  def lifetimeAllowanceCheckExceeds(isKi: Boolean, previousSchemesTotal:Int, proposedAmount: Int)(test: Boolean => Any) {
    val result = LifetimeAllowanceService.checkLifetimeAllowanceExceeded(isKi,previousSchemesTotal,proposedAmount)
    test(result)
  }

  "Sending a KI company when the previousSchemesTotal is 19999999 and the proposed amount is 1" should {
    "return false" in {

      lifetimeAllowanceCheckExceeds(isKi,previousInvestmentSchemesKiTotalBoundary,proposedAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a KI company when the previousSchemesTotal is 20000000 and the proposed amount is 1" should {
    "return true" in {

      lifetimeAllowanceCheckExceeds(isKi,previousInvestmentSchemesKiTotalOver,proposedAmount)(
        result => {
          result shouldBe true
        }
      )
    }
  }

  "Sending a non KI company when the previousSchemesTotal is 11999999 and the proposed amount is 1" should {
    "return false" in {

      lifetimeAllowanceCheckExceeds(notKi,previousInvestmentSchemesNotKiTotalBoundary,proposedAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a non KI company when the previousSchemesTotal is 12000000 and the proposed amount is 1" should {
    "return true" in {

      lifetimeAllowanceCheckExceeds(notKi,previousInvestmentSchemesNotKiTotalOver,proposedAmount)(
        result => {
          result shouldBe true
        }
      )
    }
  }

  "Sending a non KI company when the previousSchemesTotal is 0 and the proposed amount is 1" should {
    "return false" in {

      lifetimeAllowanceCheckExceeds(notKi,emptyAmount,proposedAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a KI company when the previousSchemesTotal is 0 and the proposed amount is 1" should {
    "return false" in {

      lifetimeAllowanceCheckExceeds(isKi,emptyAmount,proposedAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a non KI company when the previousSchemesTotal is 1 and the proposed amount is 0" should {
    "return false" in {

      lifetimeAllowanceCheckExceeds(notKi,proposedAmount,emptyAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a KI company when the previousSchemesTotal is 1 and the proposed amount is 0" should {
    "return false" in {

      lifetimeAllowanceCheckExceeds(isKi,proposedAmount,emptyAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }
}
