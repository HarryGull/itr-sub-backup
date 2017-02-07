# investment-tax-relief-submission

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)[![Build Status](https://travis-ci.org/hmrc/investment-tax-relief-submission.svg?branch=master)](https://travis-ci.org/hmrc/investment-tax-relief-submission) [ ![Download](https://api.bintray.com/packages/hmrc/releases/investment-tax-relief-submission/images/download.svg) ](https://bintray.com/hmrc/releases/investment-tax-relief-submission/_latestVersion)


API
----

| PATH | Supported Methods |
|------|-------------------|
|Submit an advanced assurance application:|
|```/advanced-assurance/:tavcReferenceId/submit``` | POST |
|Get the company registration details for specified safeID:|
|```/registration/registration-details/safeid/:safeID``` | GET |
|Perform a lifetime allowance check to determine if eligible to apply:|
|```/lifetime-allowance/lifetime-allowance-checker/had-previous-rfi/:hadPrevRFI/is-knowledge-intensive/:isKi/previous-schemes-total/:previousInvestmentSchemesTotal/proposed-amount/:proposedAmount``` | GET |
|Perform an annual turnover check to determine if eligible to apply:|
|```/averaged-annual-turnover/check-averaged-annual-turnover/proposed-investment-amount/:proposedInvestmentAmount/annual-turn-over/:annualTurnOver1stYear/:annualTurnOver2ndYear/:annualTurnOver3rdYear/:annualTurnOver4thYear/:annualTurnOver5thYear``` | GET |
|Perform a market eligibility check:|
|```market-criteria``` | GET |
|Perform an eligibility check for applying as knowledge intensive for the operating costs specified:|
|```/knowledge-intensive/check-ki-costs/operating-costs/:operatingCosts1stYear/:operatingCosts2ndYear/:operatingCosts3rdYear/rd-costs/:rAndDCosts1stYear/:rAndDCosts2ndYear/:rAndDCosts3rdYear``` | GET |
|Perform an eligibility check for applying as knowledge intensive for the secondary conditions specified:|
|```/knowledge-intensive/check-secondary-conditions/has-percentage-with-masters/:hasPercentageWithMasters/has-ten-year-plan/:hasTenYearPlan``` | GET |
|Perform a trade start date check to determine if eligible to apply:|
|```/trade-start-date/validate-trade-start-date/trade-start-day/:tradeStartDay/trade-start-month/:tradeStartMonth/trade-start-year/:tradeStartYear``` | GET |



Requirements
------------

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE] to run.


## Run the application


To update from Nexus and start all services from the RELEASE version instead of snapshot

```
sm --start TAVC_ALL -f
```

 
##To run the application locally execute the following:

Kill the service  ```sm --stop ITR_SUBM``` then run:
```
sbt 'run 9636' 
```


This service is part of the investment tax relief service and has dependent services.
For a full list of the dependent microservices that comprise this service please see the readme for our [Submission Frontend Service](https://github.com/hmrc/investment-tax-relief-submission-frontend/)


### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")

