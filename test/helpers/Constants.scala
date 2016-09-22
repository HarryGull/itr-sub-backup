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

package helpers

import models.{ContactDetailsModel, SubmissionRequestModel, YourCompanyNeedModel}
import play.api.libs.json.{JsValue, Json}

object Constants extends Constants

trait Constants {

  val dummySubmissionRequestModelValid = SubmissionRequestModel(
    ContactDetailsModel("James", "Harris", "0872990915","harris@gmail.com"), YourCompanyNeedModel("AA"))
  val dummySubmissionRequestModelBad = SubmissionRequestModel(
    ContactDetailsModel("James", "Harris", "0872990915","harris@badrequest.com"), YourCompanyNeedModel("AA"))
  val dummySubmissionRequestModelInternalServerError = SubmissionRequestModel(
    ContactDetailsModel("James", "Harris", "0872990915","harris@internalservererrorrequestgmail.com"), YourCompanyNeedModel("AA"))
  val dummySubmissionRequestModelForbidden = SubmissionRequestModel(
    ContactDetailsModel("James", "Harris", "0872990915","harris@forbiddengmail.com"),YourCompanyNeedModel("AA"))
  val dummySubmissionRequestModelServiceUnavailable = SubmissionRequestModel(
    ContactDetailsModel("James", "Harris", "0872990915","harris@serviceunavailablerequestgmail.com"),YourCompanyNeedModel("AA"))


  implicit val formats = Json.format[SubmissionRequestModel]
  val companyDetails = ContactDetailsModel("gary", "hull", "01952 256555", "fred@fred.com")
  val yourdetails = YourCompanyNeedModel("AA")
  lazy val validSubmissionData = new SubmissionRequestModel(companyDetails, yourdetails)

  lazy val emailBadRequestJson =
    """{"contactDetails":{"forename":"gary","surname":"hull","telephoneNumber":"01952 256555",
      |"email":"badrequest@fred.com"},"yourCompanyNeedModel":{"needAAorCS":"AA"}}""".stripMargin
  lazy val emailInternalServererrorJson =
    """{"contactDetails":{"forename":"gary","surname":"hull","telephoneNumber":"01952 256555",
      |"email":"eminternalservererrorrequest@fred.com"},"yourCompanyNeedModel":{"needAAorCS":"AA"}}""".stripMargin
  lazy val emailserviceunavailablerRequestJson =
    """{"contactDetails":{"forename":"gary","surname":"hull","telephoneNumber":"01952 256555",
      |"email":"embserviceunavailablerequest@fred.com"},"yourCompanyNeedModel":{"needAAorCS":"AA"}}""".stripMargin
  lazy val emailforbiddenRequestJson =
    """{"contactDetails":{"forename":"gary","surname":"hull","telephoneNumber":"01952 256555",
      |"email":"emforbiddenrequest@fred.com"},"yourCompanyNeedModel":{"needAAorCS":"AA"}}""".stripMargin



  lazy val badRequestSubmission = validSubmissionData.copy(this.companyDetails.copy(email ="embadrequat@fred.com"))
  lazy val internalServerErrorRequestSubmission = validSubmissionData.copy(this.companyDetails.copy(email ="eminternalservererrorrequest@fred.com"))
  lazy val serviceUnavilableErrorRequestSubmission = validSubmissionData.copy(this.companyDetails.copy(email ="embserviceunavailablerequest@fred.com"))
  lazy val forbiddenErrorRequestSubmission = validSubmissionData.copy(this.companyDetails.copy(email ="emforbiddenrequest@fred.com"))


  lazy val validJs: JsValue = Json.toJson(validSubmissionData)
  lazy val badRequestJs = Json.toJson(badRequestSubmission)
  lazy val internalServerErrorJs = Json.toJson(internalServerErrorRequestSubmission)
  lazy val forbiddenJs: JsValue = Json.toJson(forbiddenErrorRequestSubmission)
  lazy val serviceUnavilableJs: JsValue = Json.toJson(serviceUnavilableErrorRequestSubmission)
}
