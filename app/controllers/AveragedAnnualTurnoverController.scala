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
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Action}
import uk.gov.hmrc.play.microservice.controller.BaseController
import services.AveragedAnnualTurnoverService
import scala.concurrent.Future

object AveragedAnnualTurnoverController extends AveragedAnnualTurnoverController{
  override val authConnector: AuthConnector = AuthConnector
}

trait AveragedAnnualTurnoverController extends BaseController with Authorisation{

  def checkAveragedAnnualTurnover(proposedInvestmentAmount: Long, annualTurnOver1stYear: Long,annualTurnOver2ndYear: Long,annualTurnOver3rdYear: Long,
                                  annualTurnOver4thYear:Long, annualTurnOver5thYear:Long): Action[AnyContent] = Action.async {
    implicit request => authorised {
      case Authorised => Future.successful(Ok(Json.toJson{
        AveragedAnnualTurnoverService.checkAveragedAnnualTurnover(proposedInvestmentAmount,annualTurnOver1stYear,annualTurnOver2ndYear,annualTurnOver3rdYear,
          annualTurnOver4thYear,annualTurnOver5thYear)()
      }))
      case NotAuthorised => Future.successful(Forbidden)
    }
  }

}
