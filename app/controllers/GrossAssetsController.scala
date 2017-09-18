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

import auth.{Authorisation, Authorised, NotAuthorised}
import common.Constants
import connectors.AuthConnector
import play.api.libs.json._
import services.GrossAssetsService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import play.api.mvc._

import scala.util.{Failure, Success, Try}

object GrossAssetsController extends GrossAssetsController {
  override val authConnector: AuthConnector = AuthConnector
}

trait GrossAssetsController extends BaseController with Authorisation {

  def checkGrossAssetsExceeded(schemeType: String, grossAmount: Long): Action[AnyContent] = Action.async {
    implicit request =>
      authorised {
      case Authorised => {
        Try(GrossAssetsService.checkGrossAssetsExceeded(schemeType, grossAmount)) match {
          case Success(hasExceeded) => Future.successful(Ok(Json.toJson(hasExceeded)))
          case Failure(matchException) => Future.successful(BadRequest(
            Json.toJson(Map("error" -> "Invalid URL parameter", "reason" -> "Invalid scheme type"))))
        }
      }
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

  def checkGrossAssetsAfterIssueExceeded(grossAmount: Long): Action[AnyContent] = Action.async {
    implicit request =>
      authorised {
        case Authorised =>
          Future.successful(Ok(Json.toJson(GrossAssetsService.checkGrossAssetsAfterIssueExceeded(grossAmount))))
        case NotAuthorised => Future.successful(Forbidden)
      }
  }
}
