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

import auth.{Authorisation, Authorised, NotAuthorised}
import connectors.AuthConnector
import model.Error
import models.submission.DesSubmitAdvancedAssuranceModel
import play.api.libs.json._
import services.SubmissionService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.math._

object SubmissionController extends SubmissionController{
  val submissionService: SubmissionService= SubmissionService
  override val authConnector: AuthConnector = AuthConnector
}

trait SubmissionController extends BaseController with Authorisation {

  val submissionService: SubmissionService

  def submitAA(tavcReferenceId: String): Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    authorised {
      case Authorised => {
        val submissionApplicationBodyJs = request.body.validate[DesSubmitAdvancedAssuranceModel]
        submissionApplicationBodyJs.fold(
          errors => Future.successful(BadRequest(Json.toJson(Error(
            message = "Request to submit application failed with validation errors: " + errors)))),
          submitRequest => {
            submissionService.submitAA(submitRequest.copy(acknowledgementReference =
              Some(generateAcknowledgementRef(tavcReferenceId))), tavcReferenceId) map { responseReceived =>
              responseReceived.status match {
                case CREATED => Ok(responseReceived.body)
                case FORBIDDEN => Forbidden(responseReceived.body)
                case BAD_REQUEST => BadRequest(responseReceived.body)
                case SERVICE_UNAVAILABLE => ServiceUnavailable(responseReceived.body)
                case _ => InternalServerError(responseReceived.body)
              }
            }
          }
        )
      }
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

  /** Randomly generate acknowledgementReference, must be between 1 and 32 characters long**/
  private def generateAcknowledgementRef(tavcReferenceId: String): String =  {
    val ackRef = tavcReferenceId concat  (System.currentTimeMillis / 1000).toString
    ackRef.substring(0, min(ackRef.length(), 31));
  }
}
