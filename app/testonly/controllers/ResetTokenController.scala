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

//$COVERAGE-OFF$Disabling scoverage on this test only class as it is only required by our acceptance test

package testonly.controllers

import services.{ThrottleService, TokenService}

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.microservice.controller.BaseController

object ResetTokenController extends ResetTokenController{
  override val tokenService = TokenService
}

trait ResetTokenController extends BaseController{

  val tokenService : TokenService

  def resetTokens(): Action[AnyContent] = Action.async {
        implicit request =>
          tokenService.resetTokens.map(_ => Ok)
      }
  }

// $COVERAGE-ON$