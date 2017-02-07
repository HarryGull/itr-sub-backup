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

///*
// * Copyright 2017 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
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

  val testDates = (List(1,31),List(1,12),List(2016,2015,2014))
  val todaysTestDate = (1,1,2017)

  object TestTradeStartDateServiceTwoYears extends TradeStartDateService {
    override val numberOfYears: Int  = 2
    override lazy val yearsAgo = new DateTime(todaysTestDate._3,todaysTestDate._2,todaysTestDate._1,0,0).minusYears(numberOfYears)
  }

  "TradeStartDateService" should {
    "Should have a trade start date limit of two years" in {
      TradeStartDateService.numberOfYears shouldBe 2
    }
  }

  "TradeStartDateService.validateTradeStartDate" should {

    "return true if the date is within the last two years" in {
      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(testDates._1(0),testDates._2(0),testDates._3(0))
      await(result) shouldBe true
    }

    "return true if the date is within the last two years on the boundary" in {
      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(testDates._1(0),testDates._2(0),testDates._3(1))
      await(result) shouldBe true
    }

    "return true if the date is outside the last two years" in {
      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(testDates._1(1),testDates._2(1),testDates._3(2))
      await(result) shouldBe false
    }

    "return true if the date is outside the last two years on the boundary" in {
      lazy val result = TestTradeStartDateServiceTwoYears.validateTradeStartDate(testDates._1(0),testDates._2(0),testDates._3(2))
      await(result) shouldBe false
    }
  }

}
