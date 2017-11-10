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

import java.util.UUID

import org.joda.time.DateTime
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.{OneAppPerSuite}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.SessionId

class HasInvestmentTradeStartedServiceSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  val sessionId = UUID.randomUUID.toString
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))


  object TestHasInvestmentTradeStartedServiceFourMonths extends HasInvestmentTradeStartedService {
    override val numberOfMonths: Int  = 4
  }

  "HasInvestmentTradeStartedService" should {
    "Should have an investment trade start date limit of four months" in {
      HasInvestmentTradeStartedService.numberOfMonths shouldBe 4
    }
  }

  "HasInvestmentTradeStartedService.validateHasInvestmentTradeStarted" should {

    "return false if the date is today" in {
      lazy val result = TestHasInvestmentTradeStartedServiceFourMonths.validateHasInvestmentTradeStarted(DateTime.now().dayOfMonth().get(),
        DateTime.now().monthOfYear().get(),DateTime.now().year().get)
      await(result) shouldBe false
    }

    "return false if the date is within the last four months" in {
      val base = DateTime.now().minusMonths(1)
      lazy val result = TestHasInvestmentTradeStartedServiceFourMonths.validateHasInvestmentTradeStarted(base.dayOfMonth().get(),
        base.monthOfYear().get(), base.year().get)
      await(result) shouldBe false
    }

    "return false if the date is within the last four months 1 day before the boundary date" in {
      val base = DateTime.now().minusMonths(4).plusDays(1)
      lazy val result = TestHasInvestmentTradeStartedServiceFourMonths.validateHasInvestmentTradeStarted(base.dayOfMonth().get(),
        base.monthOfYear().get(),base.year().get)
      await(result) shouldBe false
    }


    "return true if the date is over four months old" in {
      val base = DateTime.now().minusMonths(5)
      lazy val result = TestHasInvestmentTradeStartedServiceFourMonths.validateHasInvestmentTradeStarted(base.dayOfMonth().get(),
        base.monthOfYear().get(),base.year().get)
      await(result) shouldBe true
    }

    "return true if the date is exactly four months old" in {
      val base = DateTime.now().minusMonths(4)
      lazy val result = TestHasInvestmentTradeStartedServiceFourMonths.validateHasInvestmentTradeStarted(base.dayOfMonth().get(),
        base.monthOfYear().get(),base.year().get)
      await(result) shouldBe true
    }


    "return true if the date is outside the last four months 1 day after the boundary date" in {
      val base = DateTime.now().minusMonths(4).minusDays(1)
      lazy val result = TestHasInvestmentTradeStartedServiceFourMonths.validateHasInvestmentTradeStarted(base.dayOfMonth().get(),
        base.monthOfYear().get(), base.year().get)
      await(result) shouldBe true
    }


  }

}
