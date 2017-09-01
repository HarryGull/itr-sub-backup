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
import uk.gov.hmrc.play.http.{HeaderCarrier}
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import org.scalatestplus.play.{OneAppPerSuite}
import uk.gov.hmrc.play.http.logging.SessionId

class TradeStartDateServiceSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  val sessionId = UUID.randomUUID.toString
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))


  object TestTradeStartDateServiceTwoYears extends TradeStartDateService {
    override val numberOfYears: Int  = 2
  }

  "TradeStartDateService" should {
    "Should have a trade start date limit of two years" in {
      TradeStartDateService.numberOfYears shouldBe 2
    }
  }

  "TradeStartDateService.validateTradeStartDate" should {

    "return true if the date is within the last two years" in {
      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(DateTime.now().dayOfMonth().get(),
        DateTime.now().monthOfYear().get(),DateTime.now().minusYears(1).year().get)
      await(result) shouldBe true
    }

//    "return true if the date is within the last two years 1 day before the boundary date" in {
//      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(DateTime.now().plusDays(1).dayOfMonth().get(),
//        DateTime.now().monthOfYear().get(),DateTime.now().minusYears(2).year().get)
//      await(result) shouldBe true
  //  }

    "return true if the date is within the last two years on the boundary" in {
      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(DateTime.now().dayOfMonth().get(),
        DateTime.now().monthOfYear().get(),DateTime.now().minusYears(2).year().get)
      await(result) shouldBe true
    }

//    "return false if the date is outside the last two years 1 day after the boundary date" in {
//      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(DateTime.now().minusDays(1).dayOfMonth().get,
//        DateTime.now().monthOfYear().get(),DateTime.now().minusYears(2).year().get)
//      await(result) shouldBe false
//    }

    "return false if the date is outside the last two years" in {
      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(DateTime.now().dayOfMonth().get(),
        DateTime.now().monthOfYear().get(),DateTime.now().minusYears(3).year().get)
      await(result) shouldBe false
    }
  }

}
