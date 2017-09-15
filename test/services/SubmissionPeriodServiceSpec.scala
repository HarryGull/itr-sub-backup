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

class SubmissionPeriodServiceSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  val sessionId = UUID.randomUUID.toString
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))
  val irrelevantDate = (1,1,1111)

  object TestSubmissionPeriodService extends SubmissionPeriodService {
    val tradeStartDateLimitMonths = 28
    val endTaxYearMonth = 4
    val endTaxYearDay = 5
  }

  "The SubmissionPeriodService" should {
    "Should have an trade start date limit of the correct amount of months" in {
      SubmissionPeriodService.tradeStartDateLimitMonths shouldBe 28
    }
  }

  "SubmissionPeriodService.latest" should {

    val dateOne = DateTime.now()
    val dateTwo = DateTime.now().minusDays(1)

    "return return the latest of two dates" in {
      val result = TestSubmissionPeriodService.latest(dateOne, dateTwo)
      await(result) shouldBe dateOne
    }

    "return the first date given if they are the same" in {
      lazy val result = {
        val result = TestSubmissionPeriodService.latest(dateOne, DateTime.now())
        await(result) shouldBe dateOne
      }
    }
  }

  "SubmissionPeriodService.endTaxYear" should {

    val currentTaxYearEnd = new DateTime(DateTime.now().getYear,
                                        TestSubmissionPeriodService.endTaxYearMonth,
                                        TestSubmissionPeriodService.endTaxYearDay, 0, 0)

    val beforeTaxYearEnd  = currentTaxYearEnd.minusDays(1)
    val afterTaxYearEnd  = currentTaxYearEnd.plusDays(1)

    "return same year tax end date if date given is before tax year end date" in {
      val result = TestSubmissionPeriodService.endOfTaxYearDate(beforeTaxYearEnd)
      result.getYear shouldBe currentTaxYearEnd.getYear
      }

    "return same year tax end date if date given is exactly the tax year end date" in {
      val result = TestSubmissionPeriodService.endOfTaxYearDate(currentTaxYearEnd)
      result.getYear shouldBe currentTaxYearEnd.getYear
    }

    "return next year tax end date if date given is after tax year end date" in {
      val result = TestSubmissionPeriodService.endOfTaxYearDate(afterTaxYearEnd)
      result.getYear shouldBe currentTaxYearEnd.plusYears(1).getYear
    }
  }


  "SubmissionPeriodService.submissionPeriodCheck" should {

    val today = DateTime.now()
    "return true if today is before the trade start date limit" in {
      val tradeStartDate = today.minusMonths(TestSubmissionPeriodService.tradeStartDateLimitMonths).plusDays(1)
      lazy val result = {
        TestSubmissionPeriodService.submissionPeriodCheck(tradeStartDate.getDayOfMonth,
          tradeStartDate.getMonthOfYear, tradeStartDate.getYear, irrelevantDate._1, irrelevantDate._2 ,irrelevantDate._3)
      }
      await(result) shouldBe true
    }

    "return false if today is on the trade start date limit" in {
      val tradeStartDate = today.minusMonths(TestSubmissionPeriodService.tradeStartDateLimitMonths)
      lazy val result = {
        TestSubmissionPeriodService.submissionPeriodCheck(tradeStartDate.getDayOfMonth,
          tradeStartDate.getMonthOfYear, tradeStartDate.getYear, irrelevantDate._1, irrelevantDate._2 ,irrelevantDate._3)
      }
      await(result) shouldBe false
    }

    "return false if today is after the trade start date limit" in {
      val tradeStartDate = today.minusMonths(TestSubmissionPeriodService.tradeStartDateLimitMonths).minusDays(1)
      lazy val result = {
        TestSubmissionPeriodService.submissionPeriodCheck(tradeStartDate.getDayOfMonth,
          tradeStartDate.getMonthOfYear, tradeStartDate.getYear, irrelevantDate._1, irrelevantDate._2 ,irrelevantDate._3)
      }
      await(result) shouldBe false
    }
//
//    "return true if today is before the end of tax year limit" in {
//      val tradeStartDate = today.minusMonths(TestSubmissionPeriodService.tradeStartDateLimitMonths).plusDays(1)
//      lazy val result = {
//        TestSubmissionPeriodService.submissionPeriodCheck(1,1,1,tradeStartDate.getDayOfMonth,
//          tradeStartDate.getMonthOfYear, tradeStartDate.getYear)
//      }
//      await(result) shouldBe true
//    }
//
//    "return false if today is on the end of tax year limit" in {
//      val tradeStartDate = today.minusMonths(TestSubmissionPeriodService.tradeStartDateLimitMonths)
//      lazy val result = {
//        TestSubmissionPeriodService.submissionPeriodCheck(1,1,1,tradeStartDate.getDayOfMonth,
//          tradeStartDate.getMonthOfYear, tradeStartDate.getYear)
//      }
//      await(result) shouldBe false
//    }
//
//    "return false if today is after end of tax year limit" in {
//      val tradeStartDate = today.minusMonths(TestSubmissionPeriodService.tradeStartDateLimitMonths).minusDays(1)
//      lazy val result = {
//        TestSubmissionPeriodService.submissionPeriodCheck(1,1,1,tradeStartDate.getDayOfMonth,
//          tradeStartDate.getMonthOfYear, tradeStartDate.getYear)
//      }
//      await(result) shouldBe false
//    }
  }

}
