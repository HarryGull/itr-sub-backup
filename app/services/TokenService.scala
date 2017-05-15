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

import models.TemporaryToken
import repositories.{Repositories, TokenMongoRepository}
import uk.gov.hmrc.play.config.ServicesConfig

import scala.concurrent.Future

object TokenService extends TokenService with ServicesConfig {
  lazy val tokenMongoRepository = Repositories.tokenRepository
  //$COVERAGE-OFF$
  val expireAfterSeconds = getConfInt("token-expiry", throw new Exception("token-expiry not found in config"))
  //$COVERAGE-ON$
}

trait TokenService  {
  val tokenMongoRepository : TokenMongoRepository
  val expireAfterSeconds: Int

  def generateTemporaryToken: Future[TemporaryToken] = {
    tokenMongoRepository.generateTemporaryToken(expireAfterSeconds)
  }

  def validateTemporaryToken(id : String): Future[Boolean] = {
    tokenMongoRepository.validateTemporaryToken(id)
  }
  //$COVERAGE-OFF$just for acceptance tests
  def resetTokens: Future[Unit] = {
    tokenMongoRepository.dropCollection
  }
  //$COVERAGE-ON$
}