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
import org.scalatestplus.play.OneServerPerSuite

import uk.gov.hmrc.play.test.UnitSpec

class AveragedAnnualTurnoverServiceSpec extends UnitSpec with OneServerPerSuite {

  val  true50Percent = (50d,Seq(100d, 100d, 100d, 100d, 100d))
  val  true101Percent = (101d,Seq(100d, 100d, 100d, 100d, 100d))
  val  false49Percent = (49d,Seq(100d, 100d, 100d, 100d, 100d))
  val  false0Percent = (0d,Seq(100d, 100d, 100d, 100d, 100d))
  val  false49Point5Decimal= (50d,Seq(101d, 101d, 101d, 101d, 101d))
  val  randomTestFail = (6865d,Seq(9100d, 39999d, 603d, 8888d, 10061d))
  val  randomTestPass = (6865.1d,Seq(9100d, 39999d, 603d, 8888d, 10061d))
  val  emptyAnnualTurnoverSeq = (50d,Seq())

  def averagedAnnualTurnoverCheck(proposedInvestmentAmount: Double, annualTurnOverXYear: Seq[Double])(test: Boolean => Any) {
    val result = AveragedAnnualTurnoverService.checkAveragedAnnualTurnover(proposedInvestmentAmount,annualTurnOverXYear:_*)()
    test(result)
  }

  "Calling checkAveragedAnnualTurnover" should {

    "return true when proposedInvestment is 50% of the mean of annual turnover" in {
      averagedAnnualTurnoverCheck(true50Percent._1,true50Percent._2)(
        result => {
          result shouldBe true
        }
      )
    }

    "return true when proposedInvestment is 101% of the mean of annual turnover" in {
      averagedAnnualTurnoverCheck(true101Percent._1,true101Percent._2)(
        result => {
          result shouldBe true
        }
      )
    }

    "return false when proposedInvestment is 49% of the mean of annual turnover" in {
      averagedAnnualTurnoverCheck(false49Percent._1,false49Percent._2)(
        result => {
          result shouldBe false
        }
      )
    }

    "return false when proposedInvestment is 0% of the mean of annual turnover" in {
      averagedAnnualTurnoverCheck(false0Percent._1,false0Percent._2)(
        result => {
          result shouldBe false
        }
      )
    }

    "return false when proposedInvestment is 49.5% of the mean of annual turnover" in {
      averagedAnnualTurnoverCheck(false49Point5Decimal._1,false49Point5Decimal._2)(
        result => {
          result shouldBe false
        }
      )
    }

    "return false when proposedInvestment is not 50% or more of random annual turnovers" in {
      averagedAnnualTurnoverCheck(randomTestFail._1,randomTestFail._2)(
        result => {
          result shouldBe false
        }
      )
    }

    "return true when proposedInvestment is exactly 50% of random annual turnovers" in {
      averagedAnnualTurnoverCheck(randomTestPass._1,randomTestPass._2)(
        result => {
          result shouldBe true
        }
      )
    }

    "return false when annual turnovers argument is empty" in {
      averagedAnnualTurnoverCheck(emptyAnnualTurnoverSeq._1,emptyAnnualTurnoverSeq._2)(
        result => {
          result shouldBe false
        }
      )
    }

  }
}
