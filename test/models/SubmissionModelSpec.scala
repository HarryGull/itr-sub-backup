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

package models

import play.api.libs.json.Json
import uk.gov.hmrc.play.test.UnitSpec

class SubmissionModelSpec extends UnitSpec{


  val testReqJson = """{"contactDetails":{"forename":"gary","surname":"hull","telephoneNumber":"01952 256555","email":"fred@fred.com"},"yourCompanyNeedModel":{"needAAorCS":"AA"}}"""
  val testResJson = """{"status":true,"formBundleId":"FBUND98763284","message":"Submission Request Successful"}"""

  // form json to model - unapply
  "call unapply successfully to create as Request Json" in {
    implicit val formats = Json.format[SubmissionRequestModel]
    val cd = ContactDetailsModel("gary", "hull", "01952 256555", "fred@fred.com")
    val yd = YourCompanyNeedModel("AA")
    val sub = new SubmissionRequestModel(cd, yd)

    val json = Json.toJson(sub)
    json.toString() shouldBe testReqJson
  }


  // form model to JSON - apply
  "call apply successfully to create Request model from Json" in {
    implicit val formats = Json.format[SubmissionResponseModel]

    val request =  Json.parse(testReqJson.toString()).as[SubmissionRequestModel]

    request.contactDetails.email  shouldBe "fred@fred.com"
    request.contactDetails.telephoneNumber  shouldBe "01952 256555"
    request.contactDetails.forename  shouldBe "gary"
    request.contactDetails.surname  shouldBe "hull"
    request.yourCompanyNeedModel.needAAorCS  shouldBe "AA"
  }

  // form json to model - unapply
  "call unapply successfully to create a Response as Json" in {
    implicit val formats = Json.format[SubmissionResponseModel]
    val response = SubmissionResponseModel(true, "FBUND98763284", "Submission Request Successful")

    val json = Json.toJson(response)
    json.toString() shouldBe testResJson

  }

  // form model to JSON - apply
  "call apply successfully to create Response model from Json" in {
    implicit val formats = Json.format[SubmissionResponseModel]

    val response =  Json.parse(testResJson.toString()).as[SubmissionResponseModel]

    response.status  shouldBe true
    response.message  shouldBe "Submission Request Successful"
    response.formBundleId  shouldBe "FBUND98763284"
  }
}
