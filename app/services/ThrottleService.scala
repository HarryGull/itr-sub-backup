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
import uk.gov.hmrc.time.DateTimeUtils
import uk.gov.hmrc.play.config.ServicesConfig
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
//TODO: import repositories.{Repositories, ThrottleMongoRepository}

object ThrottleService extends ThrottleService with ServicesConfig{
  //TODO:uncomment when available  lazy val throttleMongoRepository = repositories.throttleRepository

  //$COVERAGE-OFF$
  def dateTime = DateTimeUtils.now
  lazy val threshold = getConfInt("throttle-threshold", throw new Exception("throttle-threshold not found in config"))
  //$COVERAGE-ON$
}

trait ThrottleService{

  //TODO:uncomment when available
  // val throttleMongoRepository: ThrottleMongoRepository

  def dateTime: DateTime

  val threshold: Int

  def checkUserAccess():  Future[Boolean] = {

    //TODO: call this:
    //  throttleMongoRepository.update(getCurrentDay, threshold)
    // hardcoded for now
    Future(true)
  }

  private[services] def getCurrentDay: String = {
    dateTime.toString("yyyy-MM-dd")
  }

  //TODO: add in drop db option for testign etc
  //  def dropDb: Future[Unit] = {
  //    implicit val checkUserAccessLoggingConfig: Option[LoggingConfig] = ThrottleService.dropDbLoggingConfig
  //    logging.debug(s"Request: dropDb")
  //    throttleMongoRepository.dropDb
  //  }


}
