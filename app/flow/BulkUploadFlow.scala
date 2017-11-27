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

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, Graph}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}
import akka.util.ByteString
import akka.{Done, NotUsed}
import helpers.BulkUploadHelper
import models.{InvestorDetails, ValidationError}
import play.Logger
import services.InvestorService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

package object bulkUploadFlow {

  type Row=Seq[String]
  type RowErrors=List[ValidationError]
  type RowValidationOutcome=Either[RowErrors,Row]

  type InvestorSink = Sink[InvestorDetails,Future[Done]]
  type InvestorErrorSink  = Sink[List[ValidationError],Future[Done]]

  object BulkUploadSource {
    type File = Source[ByteString, NotUsed]
    val Separator = ","
  }

  implicit val system = ActorSystem("InvestorDetailsFlow")
  implicit val materializer = ActorMaterializer()

  def processFlow(data: String, fileId: String)(implicit hc: HeaderCarrier, ex: ExecutionContext): Unit ={
    Logger.info(s" FILE DATA INSERT TO DATABASE")
    val flow = investorDetailsCSVFlow(fileSource(data), investorDetailsSink(fileId), validationErrorsSink(fileId))
    val run = RunnableGraph.fromGraph(flow).run
    val combinedRunResult = Future.sequence(List(run._1, run._2))
    combinedRunResult map { result =>
      Logger.warn("Processing completed for file: " + fileId + "\n")
    }
  }

  /*
   * Create an akka streaming graph that parses, validates and transforms an investor
   * details CSV file
   * It has a single inlet (for the input CSV file bytes) and two outlets (one for transformed, valid details
   * and one for any validation errors)
   */
  def investorDetailsCSVFlow(csv: BulkUploadSource.File, outputSink: InvestorSink, errorSink: InvestorErrorSink)  =
    GraphDSL.create(outputSink, errorSink)((_,_)) { implicit builder =>
      (outputSink, errorSink) =>
        import GraphDSL.Implicits._

        val parse = builder.add(BulkUploadHelper.parseFromCSV)

        val validate = builder.add(new ValidationStage)

        val postValidation = builder.add(Broadcast[RowValidationOutcome](2)) // replace with Partition?

        val collectValidInvestorDetails = builder.add(Flow[RowValidationOutcome].collect {
          case Right(details) => details
        })

        val transform = builder.add(TransformationStage.rowToInvestorDetails)

        val collectValidationErrors = builder.add(Flow[RowValidationOutcome].collect {
          case Left(err) => err
        })

        csv ~> parse ~> validate ~> postValidation ~> collectValidInvestorDetails ~> transform ~> outputSink // happy flow
        postValidation ~> collectValidationErrors ~> errorSink // unhappy subflow

        ClosedShape
    }

  val outputFileOptions=Set(
    java.nio.file.StandardOpenOption.CREATE,
    java.nio.file.StandardOpenOption.WRITE,
    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING)

  def investorDetailsSink(fileId: String)(implicit ec: ExecutionContext): InvestorSink = {
    Flow[InvestorDetails]
      .map { investor =>
        InvestorService.createInvestorDetails(investor)
      }
      .toMat(Sink.ignore)(Keep.right)
  }

  def validationErrorsSink(fileId: String)(implicit ec: ExecutionContext): InvestorErrorSink = {
    Flow[List[ValidationError]]
      .map { errors =>
        // output list of errors as stream of Json objects on separate lines rather than in an array
        errors.map{error => InvestorService.createError(error)}
      }
      .toMat(Sink.ignore)(Keep.right)
  }

  def fileSource(data: String): BulkUploadSource.File = {
    Source.single(ByteString(data))
  }
}
