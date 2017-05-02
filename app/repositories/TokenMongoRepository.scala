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
import reactivemongo.api.indexes.{Index, IndexType}
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
  extends ReactiveRepository[TemporaryToken, String]("token", mongo, TemporaryToken.mongoFormats, Format(StringReads, StringWrites))
    with TokenRepository {


  def dropDb: Future[Unit] = collection.drop()

  override def indexes = Seq(
    Index(key = Seq("expireAt" -> IndexType.Ascending), name = Some("expireAtIndex"), options = BSONDocument("expireAfterSeconds" -> 0))
  )

  def generateTemporaryToken(expireAt: Int): Future[TemporaryToken] = {
      val timeBasedTemporarySecret = TemporaryToken.from(BSONObjectID.generate.stringify, "TOKEN", expireAt)
      insert(timeBasedTemporarySecret).map(_ => timeBasedTemporarySecret)
  }

  def getTemporarySecret(id: String) = findById(id)

  def validateTemporaryToken(id: String): Future[Boolean] = {
    getTemporarySecret(id).flatMap{
      case Some(token) => Future.successful(true)
      case None => Future.successful(false)
    }
  }
}