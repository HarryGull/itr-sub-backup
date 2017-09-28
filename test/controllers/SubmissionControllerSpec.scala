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

import connectors.AuthConnector
import helpers.AuthHelper._
import play.api.libs.json.{JsValue, Json}
import play.api.test.{FakeHeaders, FakeRequest}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import org.mockito.Matchers
import org.mockito.Mockito._
import play.api.test.Helpers.{FORBIDDEN, _}
import org.scalatest.mock.MockitoSugar
import uk.gov.hmrc.play.test.UnitSpec
import fixtures.SubmissionFixture
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.OneAppPerSuite
import scala.concurrent.ExecutionContext.Implicits.global
import services.{AuditService, SubmissionService}

import scala.concurrent.Future

class SubmissionControllerSpec extends UnitSpec with MockitoSugar with OneAppPerSuite with BeforeAndAfter with SubmissionFixture {

  val mockSubmissionService = mock[SubmissionService]
  val mockAuditService = mock[AuditService]

  val malformedJson =
    """
      |{
      |{
      |  "statusCode": malformed,
      |  "message": "malformed"}'"
      |}
    """.stripMargin

  val acknowledgemenRefJsAA =

    """{
                              |"acknowledgementReference": "XDTAVC000544444",
                              |	"submissionType": {
                              |		"agentReferenceNumber": "AARN1234567",
                              |		"correspondenceDetails": {
                              |			"contactName": {
                              |				"name1": "nameOne",
                              |				"name2": "nameTwo"
                              |			},
                              |			"contactDetails": {
                              |				"phoneNumber": "00000000001",
                              |				"mobileNumber": "00000000002",
                              |				"faxNumber": "00000000003",
                              |				"emailAddress": "test@test.com"
                              |			},
                              |			"contactAddress": {
                              |				"addressLine1": "addressOne",
                              |				"addressLine2": "addressTwo",
                              |				"addressLine3": "addressThree",
                              |				"addressLine4": "addressFour",
                              |				"postalCode": "AA1 1AA",
                              |				"countryCode": "GB"
                              |			}
                              |		}
                              |	}
                              |}""".stripMargin

  val acknowledgemenRefJsCS = """{
                            |"acknowledgementReference": "XDTAVC000544444",
                            |  "submissionType": {
                            |    "correspondenceDetails": {
                            |      "contactName": {
                            |        "name1": "Gerald McCaw"
                            |      },
                            |      "contactDetails": {
                            |        "emailAddress": "Gerald.McCaw@FunkyTown.com"
                            |      },
                            |      "contactAddress": {
                            |        "addressLine1": "38 UpperMarshall Street",
                            |        "addressLine2": "Post Box Aptms",
                            |        "postalCode": "SY4 9BN",
                            |        "countryCode": "GB"
                            |      }
                            |    },
                            |    "organisationType": "Limited",
                            |    "submission": {
                            |      "complianceStatement": {
                            |        "schemeType": "SEIS",
                            |        "trade": {
                            |          "businessActivity": "Preparing To Trade",
                            |          "baDescription": "Florist",
                            |          "dateTradeCommenced": "2017-01-21"
                            |        },
                            |        "investment": {
                            |          "growthJustification": "Investment Growth Justification",
                            |          "unitIssue": {
                            |            "description": "Investment Unit Issue description",
                            |            "dateOfIssue": "2012-12-11",
                            |            "unitType": "Shares",
                            |            "nominalValue": {
                            |              "amount": "55000",
                            |              "currency": "GBP"
                            |            },
                            |            "numberUnitsIssued": 3321,
                            |            "totalAmount": {
                            |              "amount": "213",
                            |              "currency": "GBP"
                            |            }
                            |          },
                            |          "organisationStatus": {
                            |            "numberOfFTEmployees": 100,
                            |            "shareOrLoanCapitalChanges": "Share Or Loan Capital Change",
                            |            "grossAssetBefore": {
                            |              "amount": "2450",
                            |              "currency": "GBP"
                            |            },
                            |            "grossAssetAfter": {
                            |              "amount": "3450",
                            |              "currency": "GBP"
                            |            }
                            |          }
                            |        },
                            |        "investorDetails": {
                            |          "investor": [
                            |            {
                            |              "investorType": "Named Investor",
                            |              "investorInfo": {
                            |                "investorDetails": {
                            |                  "companyDetails": {
                            |                    "organisationName": "Terry Tate's Automobiles",
                            |                    "companyAddress": {
                            |                      "addressLine1": "1 Weston Street",
                            |                      "addressLine2": "GrangeHall",
                            |                      "postalCode": "GH23 4WE",
                            |                      "countryCode": "GB"
                            |                    }
                            |                  }
                            |                },
                            |                "numberOfUnitsHeld": 324,
                            |                "investmentAmount": {
                            |                  "amount": "2356",
                            |                  "currency": "GBP"
                            |                }
                            |              }
                            |            }
                            |          ]
                            |        },
                            |        "repayments": {
                            |          "repayment": [
                            |            {
                            |              "repaymentDate": "2013-12-12",
                            |              "repaymentAmount": {
                            |                "amount": "2342",
                            |                "currency": "GBP"
                            |              },
                            |              "unitType": "Debentures",
                            |              "holdersName": {
                            |                "name1": "Jeffery Turner"
                            |              },
                            |              "subsidiaryName": "Sub name 1"
                            |            }
                            |          ]
                            |        },
                            |        "organisation": {
                            |          "startDate": "2012-03-31",
                            |          "orgDetails": {
                            |            "organisationName": "Sub name 1"
                            |          }
                            |        }
                            |      }
                            |    }
                            |  }
                            |}""".stripMargin

  val getSubmissionDetails =

    """{
      |"processingDate":"2015-09-22T10:30:06Z",
      |    "countReturned":"2",
      |    "countTotal":"2",
      |    "submissions":[
      |    {
      |      "formBundleNumber":"000000123456",
      |      "submissionType":"Compliance Statement",
      |      "submissionDate":"2015-09-22",
      |      "schemeType":[
      |      {
      |        "scheme":"EIS"
      |      },
      |      {
      |        "scheme":"UCT"
      |      }
      |      ],
      |      "status":"Received",
      |      "contactNoteReference":"003333333333"
      |    },
      |    {
      |      "formBundleNumber":"000000000000",
      |      "submissionType":"Advance Assurance",
      |      "submissionDate":"2015-09-22",
      |      "schemeType":[
      |      {
      |        "scheme":"EIS"
      |      },
      |      {
      |        "scheme":"UCT"
      |      }
      |      ],
      |      "status":"Rejected",
      |      "contactNoteReference":"003333333334"
      |    }
      |    ]
      |  }""".stripMargin



  val acknowledgementRefJsValAA = Json.parse(acknowledgemenRefJsAA)
  val acknowledgementRefJsValCS = Json.parse(acknowledgemenRefJsCS)
  val getSubmissionDetailsJsVal = Json.parse(getSubmissionDetails)

  implicit val hc = HeaderCarrier()

  class Setup(status: Int, response: JsValue) {
    when(mockSubmissionService.submitAA(Matchers.any(),Matchers.any())(Matchers.any(), Matchers.any()))
      .thenReturn(Future.successful(HttpResponse(status, Some(response))))
    when(mockSubmissionService.submitCS(Matchers.any(),Matchers.any())(Matchers.any(), Matchers.any()))
      .thenReturn(Future.successful(HttpResponse(status, Some(response))))
    when(mockSubmissionService.getReturnsSummary(Matchers.any())(Matchers.any(), Matchers.any()))
      .thenReturn(Future.successful(HttpResponse(status, Some(response))))

    object TestController extends SubmissionController {
      override val submissionService = mockSubmissionService
      override val authConnector = mockAuthConnector
      override val auditService = mockAuditService
    }
  }

  before {
    reset(mockAuthConnector)
  }

  "SubmissionController" should {
    "use the correct auth connector" in {
      SubmissionController.authConnector shouldBe AuthConnector
    }
    "use the correct submission service" in {
      SubmissionController.submissionService shouldBe SubmissionService
    }
  }

  "SubmissionController.submitAA with a TAVC account with status Activated and confidence level 50" when {

    "submitAA is called" should {

      "return an Created when a CREATED response is returned from service" in new Setup(CREATED, validSubmissionJsValAA) {
        setup()
        val result = TestController.submitAA(tavcRef)(FakeRequest().withBody(validSubmissionJsValAA))
        status(result) shouldBe CREATED
      }

      "return a Bad request if valid JSON already contains an acknowledgementReference" in new Setup(CREATED, acknowledgementRefJsValAA) {
        setup()
        val result = TestController.submitAA(tavcRef).apply(FakeRequest().withBody(acknowledgementRefJsValAA))
        status(result) shouldBe BAD_REQUEST
      }
    }
  }

  "SubmissionController.submitAA with a TAVC account with status NotYetActivated and confidence level 50" when {

    "submitAA is called" should {

      "return a FORBIDDEN response" in new Setup(CREATED, validSubmissionJsValAA) {
        setup("NotYetActivated")
        val result = TestController.submitAA(tavcRef)(FakeRequest().withBody(validSubmissionJsValAA))
        status(result) shouldBe FORBIDDEN
      }
    }
  }


  "SubmissionController.submitCS with a TAVC account with status Activated and confidence level 50" when {

    "submitCS is called" should {

      "return an Created when a CREATED response is returned from service" in new Setup(CREATED, validSubmissionJsValCS) {
        setup()
        val result = TestController.submitCS(tavcRef)(FakeRequest().withBody(validSubmissionJsValCS))
        status(result) shouldBe CREATED
      }

      "return a Bad request if valid JSON already contains an acknowledgementReference" in new Setup(CREATED, acknowledgementRefJsValCS) {
        setup()
        val result = TestController.submitCS(tavcRef).apply(FakeRequest().withBody(acknowledgementRefJsValCS))
        status(result) shouldBe BAD_REQUEST
      }
    }
  }

  "SubmissionController.submitCS with a TAVC account with status NotYetActivated and confidence level 50" when {

    "submitCS is called" should {

      "return a FORBIDDEN response" in new Setup(CREATED, validSubmissionJsValCS) {
        setup("NotYetActivated")
        val result = TestController.submitCS(tavcRef)(FakeRequest().withBody(validSubmissionJsValCS))
        status(result) shouldBe FORBIDDEN
      }
    }
  }

  "SubmissionController.getAASubmissionDetails with a TAVC account authorized" should {

    "return a response" in new Setup(OK, getSubmissionDetailsJsVal) {
      setup()
      val result = TestController.getReturnsSummary(tavcRef)(FakeRequest())
      status(result) shouldBe OK
      result map { response =>
        assertResult(getSubmissionDetailsJsVal)(response.body)
      }
    }
  }

  "SubmissionController.getAASubmissionDetails with a TAVC account not authorized" should {

    "return a response" in new Setup(FORBIDDEN, getSubmissionDetailsJsVal) {
      setup()
      val result = TestController.getReturnsSummary(tavcRef)(FakeRequest())
      status(result) shouldBe FORBIDDEN
    }
  }

}
