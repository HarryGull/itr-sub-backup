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
import common.MockConstants._

class GrossAssetsServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val grossAmount = 1
  val maxProposedAmountEIS = 15000000
  val maxProposedAmountSEIS = 200000
  val maxGrossAssetsAfterIssueAllowed = 16000000
  val emptyAmount = 0

  def grossAssetsCheckExceedsEIS(grossAmount: Int)(test: Boolean => Any) {
    val result = GrossAssetsService.checkGrossAssetsExceeded(testSchemeTypeEIS, grossAmount)
    test(result)
  }

  def grossAssetsCheckExceedsSEIS(grossAmount: Int)(test: Boolean => Any) {
    val result = GrossAssetsService.checkGrossAssetsExceeded(testSchemeTypeSEIS, grossAmount)
    test(result)
  }

  "Sending a gross assets amount of 1 for EIS" should {
    "return false" in {

      grossAssetsCheckExceedsEIS(grossAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a gross assets amount at the limit for EIS" should {
    "return false" in {

      grossAssetsCheckExceedsEIS(maxProposedAmountEIS)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a gross assets amount over the limit for EIS" should {
    "return true" in {

      grossAssetsCheckExceedsEIS(maxProposedAmountEIS + 1)(
        result => {
          result shouldBe true
        }
      )
    }
  }

  "Sending a gross assets amount of 1 for SEIS" should {
    "return false" in {

      grossAssetsCheckExceedsSEIS(grossAmount)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a gross assets amount at the limit for SEIS" should {
    "return false" in {

      grossAssetsCheckExceedsSEIS(maxProposedAmountSEIS)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Sending a gross assets amount over the limit for SEIS" should {
    "return true" in {

      grossAssetsCheckExceedsSEIS(maxProposedAmountSEIS + 1)(
        result => {
          result shouldBe true
        }
      )
    }
  }

  "Specifying an incorrect scheme type" should {
    "throw a MatchException" in {
      intercept[MatchError] {
        GrossAssetsService.checkGrossAssetsExceeded(invalidSchemeType, grossAmount)
      }
    }
  }

  "Sending a gross assets after issue amount over the limit" should {
    "return true" in {
      GrossAssetsService.checkGrossAssetsAfterIssueExceeded(maxGrossAssetsAfterIssueAllowed + 1) shouldBe true
    }
  }

  "Sending a gross assets after issue amount within the limit" should {
    "return true" in {
      GrossAssetsService.checkGrossAssetsAfterIssueExceeded(maxGrossAssetsAfterIssueAllowed) shouldBe false
    }
  }
}
