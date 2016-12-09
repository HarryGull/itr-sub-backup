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
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import services.FileUploadService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FileUploadController extends FileUploadController {
  override val authConnector = AuthConnector
  override lazy val fileUploadService = FileUploadService
}

trait FileUploadController extends BaseController with Authorisation {

  val fileUploadService: FileUploadService

  val createEnvelopeResponse = (envelopeID: String) => Json.parse(s"""{"envelopeID":"$envelopeID"}""")

  def createEnvelope: Action[AnyContent] = Action.async { implicit request =>
    authorised {
      case Authorised => fileUploadService.createEnvelope.map {
        case envelopeID if envelopeID.nonEmpty => Ok(createEnvelopeResponse(envelopeID))
        case _ => Logger.warn(s"[FileUploadController][createEnvelope] Error creating envelope.")
          InternalServerError
      }.recover {
        case e: Exception => InternalServerError
      }
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

  def getEnvelopeStatus(envelopeID: String): Action[AnyContent] = Action.async { implicit request =>
    authorised {
      case Authorised => fileUploadService.getEnvelopeStatus(envelopeID).map {
        result => result.status match {
          case OK => Ok(result.json)
          case _ => InternalServerError
        }
      }.recover {
        case e: Exception => InternalServerError
      }
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

  def closeEnvelope(envelopeID: String): Action[AnyContent] = Action.async { implicit request =>
    authorised {
      case Authorised => fileUploadService.closeEnvelope(envelopeID).map {
        result => result.status match {
          case CREATED => Ok
          case _ => InternalServerError
        }
      }.recover {
        case e: Exception => InternalServerError
      }
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

}
