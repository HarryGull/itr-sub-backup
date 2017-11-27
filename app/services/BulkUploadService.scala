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

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import connectors.BulkUploadConnector
import play.Logger
import play.mvc.Http.Status._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, NotFoundException}
import scala.concurrent.{ExecutionContext, Future}

object BulkUploadService extends BulkUploadService {
  override lazy val bulkUploadConnector = BulkUploadConnector
}

trait BulkUploadService {

  final val EMPTY_STRING = ""
  val bulkUploadConnector: BulkUploadConnector

  implicit val system = ActorSystem("InvestorDetailsFlow")
  implicit val materializer = ActorMaterializer()

  def getFileData(envelopeID: String, fileID: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Future[HttpResponse] = {
    bulkUploadConnector.getFileData(envelopeID, fileID).map {
      result =>
        result.status match {
        case OK => bulkUploadFlow.processFlow(result.body, fileID)
          result
        case _ => Logger.warn(s"[FileUploadService][getFileData] Error ${result.status} received.")
          result
      }
    }.recover {
      case e:NotFoundException => {
        Logger.warn(s"[FileUploadService][getFileData] Error ${e.getMessage} received for envelope Id $envelopeID"
          + "Returning Ok 200 with no data.")
        HttpResponse(OK)
      }
      case e: Exception => Logger.warn(s"[FileUploadService][getFileData] Error ${e.getMessage} received.")
        HttpResponse(INTERNAL_SERVER_ERROR)
    }
  }
}
