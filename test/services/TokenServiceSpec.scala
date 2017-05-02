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

package services

import models.TemporaryToken
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import repositories.TokenMongoRepository
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class TokenServiceSpec extends UnitSpec with MockitoSugar with WithFakeApplication{

  val mockTokenMongoRepository = mock[TokenMongoRepository]
  val tokenexpiry = 10
  val temporaryToken = TemporaryToken("123", "TEST", tokenexpiry)

  trait Setup {
    val service = new TokenService {
      val tokenMongoRepository = mockTokenMongoRepository
      val expireAfterSeconds = tokenexpiry
    }
  }

  "generateTemporaryToken" should {

    "return TemporaryToken with the provided expireAfterSeconds" in new Setup {
      when(mockTokenMongoRepository.generateTemporaryToken(Matchers.eq(tokenexpiry)))
        .thenReturn(Future.successful(TemporaryToken("123", "TEST", tokenexpiry)))

      await(service.generateTemporaryToken) shouldBe temporaryToken
    }
  }

  "validateTemporaryToken" should {

    "return true when valid temporaryToken is passed" in new Setup {
      when(mockTokenMongoRepository.validateTemporaryToken(Matchers.eq("123")))
        .thenReturn(Future.successful(true))

      await(service.validateTemporaryToken("123")) shouldBe true
    }

    "return false when invalid temporaryToken is passed" in new Setup {
      when(mockTokenMongoRepository.validateTemporaryToken(Matchers.eq("666")))
        .thenReturn(Future.successful(false))

      await(service.validateTemporaryToken("666")) shouldBe false
    }
  }
}
