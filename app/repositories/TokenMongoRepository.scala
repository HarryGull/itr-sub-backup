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

package repositories

import models.TemporaryToken
import play.api.libs.json.Format
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.Writes.StringWrites
import reactivemongo.api.DB
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait TokenRepository extends Repository[TemporaryToken, String]{
  def generateTemporaryToken(expireAt: Int) : Future[TemporaryToken]
  def validateTemporaryToken(token : String) : Future[Boolean]
}

class TokenMongoRepository(implicit mongo: () => DB)
  extends ReactiveRepository[TemporaryToken, String]("token", mongo, TemporaryToken.formats, Format(StringReads, StringWrites))
    with TokenRepository {


  def dropDb: Future[Unit] = collection.drop()

  def generateTemporaryToken(expireAfterSeconds: Int): Future[TemporaryToken] = {
    val token = Random.nextString(5)
    val temporaryToken = TemporaryToken(BSONObjectID.generate.stringify, token, expireAfterSeconds)
    insert(temporaryToken).map(_ => temporaryToken)
  }

  def validateTemporaryToken(id: String): Future[Boolean] = {
    val selector = BSONDocument("_id" -> id)
    collection.find(selector = selector).cursor[TemporaryToken]().collect[List]().flatMap {
      case h :: _ if h.id == id => Future.successful(true)
      case Nil => Future.successful(false)
    }
  }
}