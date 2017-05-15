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

import models.UserCount
import reactivemongo.api.DB
import reactivemongo.bson._
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait ThrottleRepository extends Repository[UserCount, BSONObjectID]{
  def checkUserAndUpdate(date: String, threshold: Int) : Future[Boolean]
}

class ThrottleMongoRepository(implicit mongo: () => DB)
  extends ReactiveRepository[UserCount, BSONObjectID]("throttle", mongo, UserCount.formats, ReactiveMongoFormats.objectIdFormats)
    with ThrottleRepository {


  def checkUserAndUpdate(date: String, threshold: Int): Future[Boolean] = {
    val selector = BSONDocument("_id" -> date)
    val token = Random.nextString(5)
    //TODO change this so that we query the db for the size of the users rather than returning it
    lazy val modifier = BSONDocument("$push" -> BSONDocument("users" -> token), "$set" -> BSONDocument("threshold" -> threshold))
    lazy val insertUser = collection.findAndUpdate(selector, modifier, fetchNewObject = false, upsert = true).map(_ => true)

    collection.find(selector = selector).cursor[UserCount]().collect[List]().flatMap {
      case h :: _ if h.users.size < threshold => insertUser
      case Nil => insertUser
      case _ => Future.successful(false)
    }
  }

  def dropCollection: Future[Unit] = collection.drop()
}