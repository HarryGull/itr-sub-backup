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

package common

object Constants{

  val fullTimeEquivalenceSEISLimit = 25
  val fullTimeEquivalenceEISLimit = 250
  val fullTimeEquivalenceEISWithKILimit = 500
  val fullTimeEquivalenceInvalidLimit = -0.001
  val schemeTypeEIS = "EIS"
  val schemeTypeSEIS = "SEIS"
  val schemeTypeEISWithKI = "EISKI"
  val endTaxYearDay = 5
  val endTaxYearMonth = 4
  val bulkUploadRowDelimeterLength = 10000

  val quarantined = "QUARANTINED"
  val cleaned = "CLEANED"
  val infected = "INFECTED"
  val available = "AVAILABLE"
  val error = "ERROR"

  val typeCompany = "Company"
  val typeIndividual = "Individual"

  val countryCodeGB = "GB"

}

object ResponseConstants {
  val success = "Success"
  val defaultNotFound = "The remote endpoint has indicated that no data can be found"
  val defaultBadRequest = "Bad Request"
  val defaultServiceUnavailable = "Service Unavailable"
  val defaultInternalServerError = "Internal Server Error"
  val defaultOther = "Unsuccessful return of data"
}
