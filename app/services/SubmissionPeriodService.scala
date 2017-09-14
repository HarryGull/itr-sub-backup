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

import org.joda.time.DateTime

object SubmissionPeriodService extends SubmissionPeriodService {
  val tradeStartDateLimitMonths = 28
  val endTaxYearMonth = 4
  val endTaxYearDay = 5
}

trait SubmissionPeriodService{

  val tradeStartDateLimitMonths: Int
  val endTaxYearMonth: Int
  val endTaxYearDay: Int

  def submissionPeriodCheck(tradeStartDay: Int, tradeStartMonth: Int, tradeStartYear: Int,
                          shareIssueDay: Int, shareIssueMonth: Int, shareIssueYear: Int): Boolean = {

    def tradeStartDateLimit: DateTime = new DateTime(tradeStartYear, tradeStartMonth, tradeStartDay, 0, 0).plusMonths(tradeStartDateLimitMonths)

    def taxYearLimit: DateTime = {
      val shareIssueDate = new DateTime(shareIssueYear, shareIssueMonth, shareIssueDay, 0, 0)
      endOfTaxYearDate(shareIssueDate).plusYears(2)
    }

    DateTime.now().isBefore(latest(tradeStartDateLimit, taxYearLimit))

  }

  def endOfTaxYearDate(date: DateTime): DateTime = {
    if (date.isAfter(new DateTime(date.getYear, endTaxYearMonth, endTaxYearDay, 0, 0)))
      new DateTime(date.plusYears(1).getYear, endTaxYearMonth, endTaxYearDay, 0, 0)
    else new DateTime(date.getYear, endTaxYearMonth, endTaxYearDay, 0, 0)
  }

  def latest(a: DateTime, b: DateTime): DateTime = if(a.isAfter(b)) a else b

}
