/*
 * Copyright 2016 HM Revenue & Customs
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

package connectors

import config.{MicroserviceAppConfig, WSHttp}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpGet, HttpReads, HttpResponse}

import scala.concurrent.{ExecutionContext, Future}

object RegistrationDetailsConnector extends RegistrationDetailsConnector {
  override lazy val http = WSHttp
  override lazy val serviceUrl = MicroserviceAppConfig.registrationURL
  override lazy val getRegistrationDetailsURL = MicroserviceAppConfig.getRegistrationDetailsURL
  override lazy val safeIDQuery = MicroserviceAppConfig.safeIDQuery
  override lazy val environment = MicroserviceAppConfig.desEnvironment
  override lazy val token = MicroserviceAppConfig.desToken
}

trait RegistrationDetailsConnector {

  val http: HttpGet
  val serviceUrl: String
  val getRegistrationDetailsURL: String
  val safeIDQuery: String
  val environment: String
  val token: String

  def getRegistrationDetails(safeID: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.GET[HttpResponse](s"$serviceUrl$getRegistrationDetailsURL?$safeIDQuery$safeID")(HttpReads.readRaw,
      hc.withExtraHeaders("Environment" -> environment, "Authorization" -> s"Bearer $token"))
  }
}
