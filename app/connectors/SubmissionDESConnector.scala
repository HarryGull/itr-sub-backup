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

package connectors

import config.{MicroserviceAppConfig, WSHttp}
import play.api.libs.json.{JsValue, Json, Writes}
import uk.gov.hmrc.http.logging.Authorization
import uk.gov.hmrc.http._

import scala.concurrent.{ExecutionContext, Future}

object SubmissionDESConnector extends SubmissionDESConnector {

  override val serviceUrl = MicroserviceAppConfig.submissionURL
  override def http: HttpGet with HttpPost with HttpPut = WSHttp
  override val environment = MicroserviceAppConfig.desEnvironment
  override val token = MicroserviceAppConfig.desToken
}
trait SubmissionDESConnector {

  def http: HttpGet with HttpPost with HttpPut
  val serviceUrl: String
  val environment: String
  val token: String

  def submit(jsonValue: JsValue, tavcReferenceId:String)
              (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val requestUrl = s"$serviceUrl/tax-assured-venture-capital/taxpayers/$tavcReferenceId/returns"
    val desHeaders = hc.copy(authorization = Some(Authorization(s"Bearer $token"))).withExtraHeaders("Environment" -> environment)
    http.POST[JsValue, HttpResponse](requestUrl, Json.toJson(jsonValue))(implicitly[Writes[JsValue]],HttpReads.readRaw,desHeaders, ec)
  }

  def getReturnsSummary(tavcReferenceId:String)
                       (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val requestUrl = s"$serviceUrl/tax-assured-venture-capital/taxpayers/$tavcReferenceId/returns/summary"
    val desHeaders = hc.copy(authorization = Some(Authorization(s"Bearer $token"))).withExtraHeaders("Environment" -> environment)
    http.GET[HttpResponse](requestUrl)(HttpReads.readRaw,desHeaders, ec)
  }

}
