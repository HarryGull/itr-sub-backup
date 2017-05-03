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

package models

import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.time.DateTimeUtils
case class TemporaryToken(id: String, token : String, expireAt: DateTime)

object TemporaryToken {
  import uk.gov.hmrc.mongo.json.ReactiveMongoFormats.dateTimeFormats

  implicit val mongoWrites = OWrites[TemporaryToken] { temporarytoken =>
    Json.obj(
      "_id" -> temporarytoken.id,
      "token" -> temporarytoken.token,
      "expireAt" -> temporarytoken.expireAt
    )
  }

  implicit val mongoReads: Reads[TemporaryToken] = (
    (JsPath \ "_id").read[String] and
      (JsPath \ "token").read[String] and
      (JsPath \ "expireAt").read[DateTime]
    ) ((id, token, expireAt) => TemporaryToken(id, token, expireAt))

  val mongoFormats = Format(mongoReads, mongoWrites)

  def from(id:String, token: String, expireAt: Int) = TemporaryToken(id, token, DateTimeUtils.now.plusMinutes(expireAt))

}