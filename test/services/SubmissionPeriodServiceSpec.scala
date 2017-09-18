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
import common.Constants._

class SubmissionPeriodServiceSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  val sessionId = UUID.randomUUID.toString
  implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))
  val irrelevantDate = (1,1,1111)

  object TestSubmissionPeriodService extends SubmissionPeriodService {
    val tradeStartDateLimitMonths = 28
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
      result shouldBe dateOne
    }
  }

  "SubmissionPeriodService.endTaxYear" should {

    val currentTaxYearEnd = new DateTime(DateTime.now().getYear,
                                        endTaxYearMonth,
                                        endTaxYearDay, 0, 0)

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
    val tradeStartDate = today.minusMonths(TestSubmissionPeriodService.tradeStartDateLimitMonths)
    "return true if today is before the trade start date limit" in {
      val tradeStartDateTemp =tradeStartDate.plusDays(1)
      lazy val result = {
        TestSubmissionPeriodService.submissionPeriodCheck(tradeStartDateTemp.getDayOfMonth,
          tradeStartDateTemp.getMonthOfYear, tradeStartDateTemp.getYear, irrelevantDate._1, irrelevantDate._2 ,irrelevantDate._3)
      }
      result shouldBe true
    }

    "return false if today is on the trade start date limit" in {
      lazy val result = {
        TestSubmissionPeriodService.submissionPeriodCheck(tradeStartDate.getDayOfMonth,
          tradeStartDate.getMonthOfYear, tradeStartDate.getYear, irrelevantDate._1, irrelevantDate._2 ,irrelevantDate._3)
      }
      result shouldBe false
    }

    "return false if today is after the trade start date limit" in {
      val tradeStartDateTemp =tradeStartDate.minusDays(1)
      lazy val result = {
        TestSubmissionPeriodService.submissionPeriodCheck(tradeStartDateTemp.getDayOfMonth,
          tradeStartDateTemp.getMonthOfYear, tradeStartDateTemp.getYear, irrelevantDate._1, irrelevantDate._2 ,irrelevantDate._3)
      }
      result shouldBe false
    }

    "return true when a share issue date from last year is given (Share issue date before april 6th)" in {
      lazy val result = {
        TestSubmissionPeriodService.
          submissionPeriodCheck(irrelevantDate._1, irrelevantDate._2, irrelevantDate._3,
            endTaxYearDay, endTaxYearMonth, today.minusYears(1).getYear)
      }
      result shouldBe true
    }


    "return true when a share issue date from 2 years is given (Share issue date after april 6th)" in {
      lazy val result = {
        TestSubmissionPeriodService.
          submissionPeriodCheck(irrelevantDate._1, irrelevantDate._2, irrelevantDate._3,
            endTaxYearDay + 1, endTaxYearMonth, today.minusYears(2).getYear)
      }
      result shouldBe true
    }


    "return the expectedResult when a share issue date from 2 years ago is given" in {
      val expected = {
        if(today.isBefore(new DateTime(today.getYear, endTaxYearMonth, endTaxYearMonth, 0, 0))) true else false
      }
      lazy val result = {
        TestSubmissionPeriodService.
          submissionPeriodCheck(irrelevantDate._1, irrelevantDate._2, irrelevantDate._3,
            endTaxYearDay, endTaxYearMonth, today.minusYears(2).getYear)
      }
      result shouldBe expected
    }


    "return true when the share issue date condition is the latter of the two conditions and it is after today" in {
      lazy val result = {
        TestSubmissionPeriodService.
          submissionPeriodCheck(tradeStartDate.getDayOfMonth, tradeStartDate.getMonthOfYear, tradeStartDate.getYear,
            endTaxYearDay, endTaxYearMonth, today.minusYears(1).getYear)
      }
      result shouldBe true
    }
  }

}
