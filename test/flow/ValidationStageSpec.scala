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

package bulkUploadFlow

import akka.stream.scaladsl.{Keep, Source}
import akka.stream.testkit.scaladsl.TestSink
import models.ValidationError
import uk.gov.hmrc.play.test.UnitSpec

class ValidationStageSpec extends UnitSpec{

  val validRow =  Seq("John", "Smith", "TestOne", "TestTwo", "TestThree", "A11 1AA", "GB", "no")
  val invalidRow = Seq("not", "enough", "columns", "in", "this", "investor", "row")

  val stream = collection.immutable.Seq(validRow, invalidRow)


  "ValidationStage" should {

    "emit accumulated row outcome elements" in {

      val (_, sink) = Source(stream)
        .via(new ValidationStage)
        .toMat(TestSink.probe)(Keep.both)
        .run()

      sink.request(2)
      sink.expectNext(Right(validRow), Left(List(ValidationError(2, invalidRow.size,"The row must have at least 8 columns"))))
    }
  }

}
