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

import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec

class AnnualLimitServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach{

  val halfMaximum = 2500000
  val previousSchemesTotal = 2500000
  val maxProposedAmount = 5000000
  val emptyAmount = 0
  val negativeAmount = -1

  def lifetimeAllowanceCheckExceeds(previousSchemesTotal:Int, proposedAmount: Int)(test: Boolean => Any) {
    val result = AnnualLimitService.checkLimitExceeded(previousSchemesTotal,proposedAmount)
    test(result)
  }

  s"Sending a previousSchemesTotal that is $halfMaximum and total amount raised that is $halfMaximum" should {
    "return false as not exceeded" in {
      lifetimeAllowanceCheckExceeds(halfMaximum, halfMaximum)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  s"Sending a previousSchemesTotal that is $halfMaximum and total amount raised that is ${halfMaximum + 1}" should {
    "return true as exceeded" in {
      lifetimeAllowanceCheckExceeds(halfMaximum, halfMaximum + 1)(
        result => {
          result shouldBe true
        }
      )
    }
  }

  s"Sending a previousSchemesTotal that is $emptyAmount and total amount raised that is $emptyAmount" should {
    "return false as not exceeded" in {
      lifetimeAllowanceCheckExceeds(halfMaximum, halfMaximum)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  s"Sending a previousSchemesTotal that is $negativeAmount and total amount raised that is $negativeAmount" should {
    "return false as not exceeded" in {
      lifetimeAllowanceCheckExceeds(halfMaximum, halfMaximum)(
        result => {
          result shouldBe false
        }
      )
    }
  }

}
