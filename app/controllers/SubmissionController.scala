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

package controllers

import model.Error
import models.{SubmissionRequestModel}
import play.api.libs.json._
import services.SubmissionService
import uk.gov.hmrc.play.microservice.controller.BaseController
import scala.concurrent.Future
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

object SubmissionController extends SubmissionController

trait SubmissionController extends BaseController {

  def submitAA: Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    val submissionApplicationBodyJs = request.body.validate[SubmissionRequestModel]
    submissionApplicationBodyJs.fold(
      errors => Future.successful(BadRequest(Json.toJson(Error(message="Request to submit application failed with validation errors: " + errors)))),
      submitRequest => {
        SubmissionService.submitAA(submitRequest) map { responseReceived =>
          responseReceived.status match {
            case CREATED => Ok(responseReceived.body)
            case NOT_FOUND => NotFound(responseReceived.body)
            case BAD_REQUEST => BadRequest(responseReceived.body)
            case SERVICE_UNAVAILABLE => ServiceUnavailable(responseReceived.body)
            case _ => InternalServerError(responseReceived.body)
          }
        }
      }
    )
  }

}
