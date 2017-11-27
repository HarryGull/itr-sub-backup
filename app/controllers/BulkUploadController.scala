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

package controllers

import auth.Authorisation
import common.Constants
import connectors.AuthConnector
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, BodyParsers}
import services.BulkUploadService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BulkUploadController extends BulkUploadController {
  override val authConnector = AuthConnector
  override lazy val bulkUploadService = BulkUploadService
}

trait BulkUploadController extends BaseController with Authorisation {

  val bulkUploadService: BulkUploadService

  def getFileData(envelopeID: String, fileID: String): Action[AnyContent] = Action.async { implicit request =>
    bulkUploadService.getFileData(envelopeID, fileID).map {
      result => result.status match {
        case OK =>  Ok(result.body)
        case _ => InternalServerError
      }
    }.recover {
      case e: Exception => InternalServerError
    }
  }

  def processFlow: Action[JsValue] = Action.async(BodyParsers.parse.json) { implicit request =>
    Logger.info(" CALLBACK FROM FILE UPLOAD SERVICE \n" + Json.prettyPrint(request.body))
    (request.body \ "status").as[String] match {
      case Constants.available =>
        bulkUploadService.getFileData((request.body \ "envelopeId").as[String], (request.body \ "fileId").as[String]).map {
          result => result.status match {
            case OK =>  Ok(result.body)
            case _ => InternalServerError
          }
        }.recover {
          case e: Exception => InternalServerError
        }
      case _ => Future.successful(InternalServerError)
    }
  }
}
