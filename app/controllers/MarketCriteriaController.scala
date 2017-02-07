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
import services.MarketCriteriaService
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

object MarketCriteriaController extends MarketCriteriaController{
  override lazy val authConnector = AuthConnector
}

trait MarketCriteriaController extends BaseController with Authorisation {
  def checkMarketCriteria(newGeographical: Boolean, newProduct: Boolean): Action[AnyContent] = Action.async { implicit request =>
    authorised {
      case Authorised => Future.successful(Ok(Json.toJson(MarketCriteriaService.checkMarketCriteria(newGeographical,newProduct))))
      case NotAuthorised => Future.successful(Forbidden)
    }
  }
}
