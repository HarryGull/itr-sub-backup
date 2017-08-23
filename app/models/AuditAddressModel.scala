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

import AuditAddressModel._
import play.api.libs.json._
import play.api.libs.functional.syntax._


case class AuditAddressModel(addressLine1 : Option[String] = Some(noValueProvided),
                             addressLine2 : Option[String] = Some(noValueProvided),
                             addressLine3 : Option[String] = Some(noValueProvided),
                             addressLine4 : Option[String] = Some(noValueProvided),
                             postCode : Option[String] = Some(noValueProvided),
                             country : Option[String] = Some(noValueProvided)
                            )

object AuditAddressModel {
  private val noValueProvided =  "Not Provided"

  implicit val writesAuditAddress = Json.writes[AuditAddressModel]

  implicit val readsAuditAddress: Reads[AuditAddressModel] = (
    (__ \ "addressLine1").readNullable[String] and
      (__ \ "addressLine2").readNullable[String] and
      (__ \ "addressLine3").readNullable[String] and
      (__ \ "addressLine4").readNullable[String] and
      (__ \ "postalCode").readNullable[String] and
      (__ \ "countryCode").readNullable[String]
    ) (AuditAddressModel.apply _)
}
