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


import org.joda.time.DateTime
import org.mockito.Matchers
import org.scalatest.mock.MockitoSugar
//TODO: import repositories.{Repositories, ThrottleMongoRepository}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import org.mockito.Mockito._


class ThrottleServiceSpec extends UnitSpec with MockitoSugar with WithFakeApplication{

  //TODO: val mockThrottleMongoRepository = mock[ThrottleMongoRepository]

  trait Setup {
    val service = new ThrottleService {
      //TODO:val throttleMongoRepository = mockThrottleMongoRepository
      override def dateTime = DateTime.parse("2000-02-01")
      val threshold = 10
    }
  }

  "getCurrentDay" should {
    "return the current day" in new Setup {
      service.getCurrentDay shouldBe "2000-02-01"
    }

    //TODO: Need to modify when repositiry created in seperate task
//    "updateUserCount" should {
//
//      "return true when updating user count on a new collection" in new Setup {
//        when(mockThrottleMongoRepository.update(Matchers.eq("2000-02-01"), Matchers.eq(10)))
//          .thenReturn(Future.successful(1))
//
//        await(service.checkUserAccess) shouldBe true
//      }
//
//      "return true when user threshold is reached" in new Setup {
//        when(mockThrottleMongoRepository.update(Matchers.eq("2000-02-01"), Matchers.eq(10)))
//          .thenReturn(Future.successful(10))
//        when(mockThrottleMongoRepository.compensate(Matchers.eq("2000-02-01"), Matchers.eq(10)))
//          .thenReturn(Future.successful(10))
//
//        await(service.checkUserAccess) shouldBe true
//      }
//
//      "return false when user threshold is over the limit" in new Setup {
//        when(mockThrottleMongoRepository.update(Matchers.eq("2000-02-01"), Matchers.eq(10))
//          .thenReturn(Future.successful(15))
//        when(mockThrottleMongoRepository.compensate(Matchers.eq("2000-02-01"), Matchers.eq(10)))
//          .thenReturn(Future.successful(10))
//
//        await(service.checkUserAccess) shouldBe false
//      }
//    }
  }
}
