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

import common.Constants

object GrossAssetsService extends GrossAssetsService{
  val maxGrossAssetsAllowedEIS: Int = 15000000
  val maxGrossAssetsAllowedSEIS: Int = 200000
  val maxGrossAssetsAfterIssueAllowed: Int = 16000000
}

trait GrossAssetsService{

  val maxGrossAssetsAllowedEIS: Int
  val maxGrossAssetsAllowedSEIS: Int
  val maxGrossAssetsAfterIssueAllowed: Int

  def checkGrossAssetsExceeded(schemeType: String, grossAmount: Long): Boolean = {
    schemeType match {
      case Constants.schemeTypeEIS => grossAmount > maxGrossAssetsAllowedEIS
      case Constants.schemeTypeSEIS => grossAmount > maxGrossAssetsAllowedSEIS
    }
  }

  def checkGrossAssetsAfterIssueExceeded(grossAmount: Long): Boolean = {
    grossAmount > maxGrossAssetsAfterIssueAllowed
  }
}
