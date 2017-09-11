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

import auth.{NotAuthorised, Authorised, Authorisation}
import connectors.AuthConnector
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, BodyParsers, Action}
import services.{EmployeeFullTimeEquivalenceService}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

object EmployeeFullTimeEquivalentController extends EmployeeFullTimeEquivalentController{
  override val authConnector = AuthConnector
  override val employeeFullTimeEquivalenceService = EmployeeFullTimeEquivalenceService
}

trait EmployeeFullTimeEquivalentController extends BaseController with Authorisation{

  val employeeFullTimeEquivalenceService: EmployeeFullTimeEquivalenceService

  def checkFullTimeEquivalence(schemeType: String, numberOfFullTimeEquivalentEmployees: String): Action[AnyContent] =
    Action.async { implicit request =>
    authorised {
      case Authorised => {
        Future.successful(employeeFullTimeEquivalenceService.checkFullTimeEquivalence(
          schemeType, numberOfFullTimeEquivalentEmployees))
      }
      case NotAuthorised => Future.successful(Forbidden)
    }

  }
}
