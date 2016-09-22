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

import config.WSHttp
import models.SubmissionRequestModel
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.play.http._

import scala.concurrent.{Future, ExecutionContext}

object SubmissionDESConnector extends SubmissionDESConnector with ServicesConfig {

  override val serviceUrl = baseUrl("investment-tax-relief-submission-dynamic-stub")
  override def http: HttpGet with HttpPost with HttpPut = WSHttp
}
trait SubmissionDESConnector {

  def http: HttpGet with HttpPost with HttpPut
  val serviceUrl: String

  def submitAA(submissionRequest: SubmissionRequestModel)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
      val requestUrl = s"$serviceUrl/investment-tax-relief/advanced-assurance/submit"
      http.POST[JsValue, HttpResponse](requestUrl, Json.toJson(submissionRequest))
  }
}
