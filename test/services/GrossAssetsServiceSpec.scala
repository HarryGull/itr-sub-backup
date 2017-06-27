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

class GrossAssetsServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val grossAmount = 1
  val maxProposedAmount = 200000
  val emptyAmount = 0

  def grossAssetsCheckExceeds(grossAmount: Int)(test: Boolean => Any) {
    val result = GrossAssetsService.checkGrossAssetsExceeded(grossAmount)
    test(result)
  }

  "Sending a gross assets amount is 1" should {
    "return false" in {

      grossAssetsCheckExceeds(grossAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a gross assets amount is at the limit" should {
    "return false" in {

      grossAssetsCheckExceeds(maxProposedAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a gross assets amount is over the limit" should {
    "return true" in {

      grossAssetsCheckExceeds(maxProposedAmount + 1)(
        result => {
          result shouldBe true
        }
      )
    }
  }

}
