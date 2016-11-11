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
import play.Logger
import play.api.mvc.{Action, AnyContent}
import services.RegistrationDetailsService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object RegistrationDetailsController extends RegistrationDetailsController {
  override lazy val authConnector = AuthConnector
  override lazy val registrationDetailsService = RegistrationDetailsService
}

trait RegistrationDetailsController extends BaseController with Authorisation {

  val registrationDetailsService: RegistrationDetailsService

  def getRegistrationDetails(safeID: String): Action[AnyContent] = Action.async { implicit request =>
    authorised {
      case Authorised => {
        Logger.info(s"[RegistrationDetailsController][getRegistrationDetails] - Authorised")
        registrationDetailsService.getRegistrationDetails(safeID)
      }
      case NotAuthorised => {
        Logger.info(s"[RegistrationDetailsController][getRegistrationDetails] - UnAuthorised")
        Future.successful(Forbidden)
      }
    }
  }

}
