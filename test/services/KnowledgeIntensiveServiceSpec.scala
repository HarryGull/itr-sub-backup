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
import org.scalatestplus.play.OneServerPerSuite

import uk.gov.hmrc.play.test.UnitSpec

class KnowledgeIntensiveServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with OneServerPerSuite {

  val rAndDCostsZero = List(100, 100, 100, 0, 0, 0)
  val rAndDCostsTen = List(100, 100, 100, 10, 10, 10)
  val rAndDCostsFifteen = List(100, 100, 100, 15, 0, 0)
  val negativeOperatingCosts = List(-100, -100, -100, 0, 0, 0)
  val negativeRAndDCosts = List(-100, -100, -100, -20, -20, -20)

  def kICostsCheck(costs: List[Int])(test: Boolean => Any) {
    val result = KnowledgeIntensiveService.validateKICosts(costs(0),costs(1),costs(2),costs(3),costs(4),costs(5))
    test(result)
  }

  def secondaryKIConditionsCheck(hasPercentageWithMasters: Boolean, hasTenYearPlan: Boolean)(test: Boolean => Any) {
    val result = KnowledgeIntensiveService.validateSecondaryKIConditions(hasPercentageWithMasters,hasTenYearPlan)
    test(result)
  }

  "Calling checkRAndDCosts" should {

    "with all 0 percent R and D's should return false" in {
      kICostsCheck(rAndDCostsZero)(
        result => {
          result shouldBe false
        }
      )
    }

    "with all ten percent R and D's should return true" in {
      kICostsCheck(rAndDCostsTen)(
        result => {
          result shouldBe true
        }
      )
    }

    "with a single 15 percent R And D should return true" in {
      kICostsCheck(rAndDCostsFifteen)(
        result => {
          result shouldBe true
        }
      )
    }

    "with all negative Operating costs should return false" in {
      kICostsCheck(negativeOperatingCosts)(
        result => {
          result shouldBe false
        }
      )
    }

    "with all negative R and D's should return false" in {
      kICostsCheck(negativeRAndDCosts)(
        result => {
          result shouldBe false
        }
      )
    }
  }

  "Calling secondaryKIConditionsCheck" should {

    "return true with input (true,true)" in {
      secondaryKIConditionsCheck(true, true)(
          result => {
            result shouldBe true
          }
      )
    }

    "return true with input (false,true)" in {
      secondaryKIConditionsCheck(false, true)(
          result => {
            result shouldBe true
          }
      )
    }

    "return true with input (true,false)" in {
      secondaryKIConditionsCheck(true, false)(
          result => {
            result shouldBe true
          }
      )
    }

    "return false with input (false,false)" in {
      secondaryKIConditionsCheck(false, false)(
          result => {
            result shouldBe false
          }
      )
    }
  }
}
