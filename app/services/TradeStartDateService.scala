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

import java.time.{LocalDate, ZoneId}
import java.util.{Calendar, Date}

import org.apache.commons.lang3.time.DateUtils
import org.joda.time.DateTime
import play.api.Logger

object TradeStartDateService extends TradeStartDateService{
  val numberOfYears = 2
}

trait TradeStartDateService{

  val numberOfYears: Int

  def validateTradeStartDate(day: Int, month: Int, year: Int): Boolean = {
    val dateYearsAgo = DateTime.now().minusYears(numberOfYears);
    val tradeStartDate = new DateTime(year,month,day, 0, 0)
    tradeStartDate.isAfter(dateYearsAgo) || DateUtils.isSameDay(tradeStartDate.toDate, dateYearsAgo.toDate)
  }

}
