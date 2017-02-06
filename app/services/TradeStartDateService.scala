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

import org.joda.time.DateTime
import play.api.Logger

object TradeStartDateService extends TradeStartDateService{
  val years = 2
}

trait TradeStartDateService{
  val years: Int

  def validateTradeStartDate(day: Int, month: Int, year: Int): Boolean = {
    val cal = Calendar.getInstance();
    cal.add(Calendar.YEAR, -(years)); // to get previous year add -1
    val dateYearsAgo = cal.getTime();
    lazy val tradeStartDate = getDateFromCalendar()

    def getDateFromCalendar(): Date = {
      val cal = Calendar.getInstance();
      cal.set(Calendar.YEAR, year);
      cal.set(Calendar.MONTH, month);
      cal.set(Calendar.DAY_OF_MONTH, day);
      cal.getTime()
    }
    Logger.warn("TRADESTARTDATESERVICE: " + "Date Two years ago: " + dateYearsAgo)
    Logger.warn("TRADESTARTDATESERVICE: " + "Trade sTART dATE: " + tradeStartDate)
    tradeStartDate.after(dateYearsAgo)
  }

}
