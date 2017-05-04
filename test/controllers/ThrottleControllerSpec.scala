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
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import services.{AuditService, ThrottleService}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import play.api.test.Helpers._
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class ThrottleControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  implicit val system = ActorSystem("CR")
  implicit val materializer = ActorMaterializer()

  val mockAuditService = mock[AuditService]

  val mockThrottleService = mock[ThrottleService]

  implicit val hc = HeaderCarrier()

  trait Setup {
    val controller = new ThrottleController {
      override val throttleService = mockThrottleService
      override val auditService = mockAuditService
    }
  }


  "ThrottleController" should {
    "use the correct throttle sevice" in {
      ThrottleController.throttleService shouldBe ThrottleService
    }
    "use the correct auditService" in {
      ThrottleController.auditService shouldBe AuditService
    }
  }


  "checkThrottle" should {

    "return a 200 when true" in new Setup {

      when(mockThrottleService.checkUserAccess)
        .thenReturn(Future.successful(true))

      val result = controller.checkUserAccess()(FakeRequest())
      status(result) shouldBe OK
      await(jsonBodyOf(result)) shouldBe Json.toJson(true)
    }

    "return a 200 when false" in new Setup {

      when(mockThrottleService.checkUserAccess)
        .thenReturn(Future.successful(false))

      val result = controller.checkUserAccess()(FakeRequest())
      status(result) shouldBe OK
      await(jsonBodyOf(result)) shouldBe Json.toJson(false)
    }

  }

}
