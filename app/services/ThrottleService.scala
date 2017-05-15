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
import repositories.{Repositories, ThrottleMongoRepository}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.time.DateTimeUtils

import scala.concurrent.Future

object ThrottleService extends ThrottleService with ServicesConfig {
  lazy val throttleMongoRepository = Repositories.throttleRepository
  //$COVERAGE-OFF$
  def dateTime: DateTime = DateTimeUtils.now
  val threshold = getConfInt("throttle-threshold", throw new Exception("throttle-threshold not found in config"))
  //$COVERAGE-ON$
}

trait ThrottleService  {
  val throttleMongoRepository : ThrottleMongoRepository
  def dateTime: DateTime
  val threshold: Int

  def checkUserAccess: Future[Boolean] = {
    val date = getCurrentDay
    throttleMongoRepository.checkUserAndUpdate(date, threshold)
  }

  def resetThrottle: Future[Unit] = {
    throttleMongoRepository.dropCollection
  }

  //$COVERAGE-OFF$just for acceptance tests
  private[services] def getCurrentDay: String = {
    dateTime.toString("yyyy-MM-dd")
  }
  //$COVERAGE-ON$
}