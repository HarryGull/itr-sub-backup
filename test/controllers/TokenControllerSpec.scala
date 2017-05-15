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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import models.TemporaryToken
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.{AuditService, TokenService}
import uk.gov.hmrc.play.test.UnitSpec
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.{HttpResponse, HeaderCarrier}

import scala.concurrent.Future

class TokenControllerSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  implicit val system = ActorSystem("CR")
  implicit val materializer = ActorMaterializer()

  val mockAuditService = mock[AuditService]

  val mockTokenService = mock[TokenService]

  implicit val hc = HeaderCarrier()

  trait Setup {
    val controller = new TokenController {
      override val tokenService = mockTokenService
      override val auditService = mockAuditService
    }
    val temporaryToken = TemporaryToken("590855a2640000d000592d3e","TOKEN", DateTime.now())
   // val httpResponse = HttpResponse(OK, Some(Json.toJson(temporaryToken)))
  }

  "The token controller" should {
    "use the correct audit service" in {
      TokenController.auditService shouldBe AuditService
    }
    "use the correct token service" in {
      TokenController.tokenService shouldBe TokenService
    }
  }


  "generateTemporaryToken" should {

    "return an Ok with a token in the response body" in new Setup {

      when(mockTokenService.generateTemporaryToken)
        .thenReturn(Future.successful(temporaryToken))

      val result = controller.generateTemporaryToken()(FakeRequest())
      status(result) shouldBe OK
      await(jsonBodyOf(result)) shouldBe Json.toJson(temporaryToken)
    }
  }

  "validateTemporaryToken" should {

    "return an Ok with a boolean in the response body" in new Setup {

      when(mockTokenService.validateTemporaryToken(temporaryToken.id))
        .thenReturn(Future.successful(true))

      val result = controller.validateTemporaryToken(temporaryToken.id)(FakeRequest())
      status(result) shouldBe OK
      await(jsonBodyOf(result)) shouldBe Json.toJson(true)
    }
  }




}
