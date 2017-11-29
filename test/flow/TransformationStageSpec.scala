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

package flow

import bulkUploadFlow.TransformationStage
import uk.gov.hmrc.play.test.UnitSpec

class TransformationStageSpec extends UnitSpec{

  val validInvestorHolding = Seq("28-02-12","tis is a good single holding","2000.12","24")

  "calling fromHoldingColumns on the TransformationStage object" should {

    lazy val investorHolding = TransformationStage.fromHoldingColumns(validInvestorHolding)

    "have the correct date" in {
      investorHolding.dateIssued shouldBe "28-02-12"
    }

    "have the correct description" in {
      investorHolding.description shouldBe "tis is a good single holding"
    }

    "have the correct amount" in {
      investorHolding.amount shouldBe 2000.12d
    }

    "have the correct number" in {
      investorHolding.number shouldBe 24
    }

  }
}
