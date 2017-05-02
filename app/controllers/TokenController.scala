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

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.microservice.controller.BaseController
import services.{TokenService, AuditService}
import scala.concurrent.ExecutionContext.Implicits.global


object TokenController extends TokenController{
  val auditService = AuditService
  override val tokenService = TokenService
}

trait TokenController extends BaseController{

  val auditService : AuditService
  val tokenService : TokenService

  def generateTemporaryToken(): Action[AnyContent] = Action.async { implicit request =>
      tokenService.generateTemporaryToken map {
        token => Ok(Json.toJson(token))
      }
  }

  def validateTemporaryToken(id: String): Action[AnyContent] = Action.async { implicit request =>
    tokenService.validateTemporaryToken(id) map {
      validated => Ok(Json.toJson(validated))
    }
  }
}
