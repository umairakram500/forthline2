package com.fourthline.sdksample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fourthline.core.CountryNetworkModel
import com.fourthline.core.DocumentType
import com.fourthline.sdk.config.OrcaStandaloneDocumentConfig
import com.fourthline.sdk.stepbuilder.DataSource
import com.fourthline.sdk.stepbuilder.LocalDataSource
import com.fourthline.sdk.stepbuilder.Orca
import com.fourthline.sdk.stepbuilder.OrcaFinishReason
import com.fourthline.sdksample.databinding.ActivityOrcaSampleBinding

/**
 * Sample supported countries.
 */
private const val SUPPORTED_COUNTRIES_AS_STRING = """
        [{"issuingCountry":"ARG","idDocuments":[{"type":"Passport","nfcIssueDate":"2012-06-01T00:00:00Z","validityPeriod":10,"nationalities":["ARG"]}]},{"issuingCountry":"AUS","idDocuments":[{"type":"Passport","nfcIssueDate":"2005-09-02T00:00:00Z","validityPeriod":10,"nationalities":["AUS"]}]},{"issuingCountry":"AUT","idDocuments":[{"type":"Passport","nfcIssueDate":"2009-06-15T00:00:00Z","validityPeriod":10,"nationalities":["AUT"]},{"type":"NationalIDCard","nationalities":["AUT"]},{"type":"ResidencePermit","nfcIssueDate":"2011-07-01T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"BEL","idDocuments":[{"type":"Passport","nfcIssueDate":"2014-05-01T00:00:00Z","validityPeriod":7,"nationalities":["BEL"]},{"type":"NationalIDCard","nfcIssueDate":"2020-01-06T00:00:00Z","validityPeriod":10,"nationalities":["BEL"]},{"type":"ResidencePermit","nfcIssueDate":"2013-05-19T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"BRA","idDocuments":[{"type":"Passport","nfcIssueDate":"2010-10-25T00:00:00Z","validityPeriod":5,"nationalities":["BRA"]}]},{"issuingCountry":"BGR","idDocuments":[{"type":"Passport","nfcIssueDate":"2010-03-29T00:00:00Z","validityPeriod":5,"nationalities":["BGR"]}]},{"issuingCountry":"CAN","idDocuments":[{"type":"Passport","nfcIssueDate":"2013-01-01T00:00:00Z","validityPeriod":10,"nationalities":["CAN"]}]},{"issuingCountry":"CHE","idDocuments":[{"type":"Passport","nfcIssueDate":"2010-03-01T00:00:00Z","validityPeriod":10,"nationalities":["CHE"]},{"type":"NationalIDCard","nationalities":["CHE"]},{"type":"ResidencePermit","nfcIssueDate":"2011-08-17T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"CHN","idDocuments":[{"type":"Passport","nfcIssueDate":"2013-02-02T00:00:00Z","validityPeriod":10,"nationalities":["CHN"]}]},{"issuingCountry":"CYP","idDocuments":[{"type":"Passport","nfcIssueDate":"2010-11-29T00:00:00Z","validityPeriod":10,"nationalities":["CYP"]}]},{"issuingCountry":"CZE","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-09-01T00:00:00Z","validityPeriod":10,"nationalities":["CZE"]},{"type":"NationalIDCard","nationalities":["CZE"]}]},{"issuingCountry":"DNK","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-08-01T00:00:00Z","validityPeriod":10,"nationalities":["DNK"]},{"type":"ResidencePermit","nfcIssueDate":"2008-01-01T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"ESP","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-08-14T00:00:00Z","validityPeriod":10,"nationalities":["ESP"]},{"type":"NationalIDCard","nfcIssueDate":"2015-01-02T00:00:00Z","validityPeriod":10,"nationalities":["ESP"]}]},{"issuingCountry":"EST","idDocuments":[{"type":"Passport","nfcIssueDate":"2014-06-01T00:00:00Z","validityPeriod":5,"nationalities":["EST"]},{"type":"ResidencePermit","nfcIssueDate":"2011-01-01T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"FIN","idDocuments":[{"type":"Passport","nfcIssueDate":"2012-08-21T00:00:00Z","validityPeriod":5,"nationalities":["FIN"]},{"type":"NationalIDCard","nationalities":["FIN"]},{"type":"ResidencePermit","nfcIssueDate":"2012-01-01T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"FRA","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-04-12T00:00:00Z","validityPeriod":10,"nationalities":["FRA"]},{"type":"NationalIDCard","nfcIssueDate":"2021-03-15T00:00:00Z","validityPeriod":10,"nationalities":["FRA"]},{"type":"ResidencePermit","nfcIssueDate":"2011-06-20T00:00:00Z","validityPeriod":10,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"GBR","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-02-06T00:00:00Z","validityPeriod":10,"nationalities":["GBR"]}]},{"issuingCountry":"GRC","idDocuments":[{"type":"Passport","nfcIssueDate":"2011-08-27T00:00:00Z","validityPeriod":5,"nationalities":["GRC"]},{"type":"NationalIDCard","nationalities":["GRC"]},{"type":"ResidencePermit","nfcIssueDate":"2013-06-04T00:00:00Z","validityPeriod":10,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"HKG","idDocuments":[{"type":"Passport","nfcIssueDate":"2007-02-05T00:00:00Z","validityPeriod":10,"nationalities":["HKG"]}]},{"issuingCountry":"HRV","idDocuments":[{"type":"Passport","nfcIssueDate":"2009-06-29T00:00:00Z","validityPeriod":10,"nationalities":["HRV"]},{"type":"NationalIDCard","nfcIssueDate":"2021-08-02T00:00:00Z","validityPeriod":5,"nationalities":["HRV"]}]},{"issuingCountry":"HUN","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-08-29T00:00:00Z","validityPeriod":10,"nationalities":["HUN"]},{"type":"NationalIDCard","nfcIssueDate":"2016-01-01T00:00:00Z","validityPeriod":5,"nationalities":["AUT","BEL","HRV","CHE","CZE","ESP","FIN","FRA","GRC","HUN","IRL","ITA","LIE","LUX","MLT","NLD","POL","PRT","SVN","SWE"]}]},{"issuingCountry":"IND","idDocuments":[{"type":"Passport","nfcIssueDate":"2008-01-01T00:00:00Z","nationalities":["IND"]}]},{"issuingCountry":"IRL","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-10-01T00:00:00Z","validityPeriod":10,"nationalities":["IRL"]},{"type":"NationalIDCard","nfcIssueDate":"2015-10-02T00:00:00Z","validityPeriod":5,"nationalities":["IRL"]},{"type":"ResidencePermit","nfcIssueDate":"2017-10-23T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"ISL","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-05-23T00:00:00Z","validityPeriod":5,"nationalities":["ISL"]},{"type":"ResidencePermit","nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"ITA","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-10-26T00:00:00Z","validityPeriod":10,"nationalities":["ITA"]},{"type":"NationalIDCard","nfcIssueDate":"2016-07-04T00:00:00Z","validityPeriod":10,"nationalities":["AUT","BEL","HRV","CHE","CZE","ESP","FIN","FRA","GRC","HUN","IRL","ITA","LIE","LUX","MLT","NLD","POL","PRT","SVN","SWE"]},{"type":"PaperID","nationalities":["ITA"]},{"type":"ResidencePermit","nfcIssueDate":"2013-11-06T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]},{"type":"DrivingLicense","nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"JPN","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-03-20T00:00:00Z","validityPeriod":10,"nationalities":["JPN"]}]},{"issuingCountry":"KOR","idDocuments":[{"type":"Passport","nfcIssueDate":"2008-08-25T00:00:00Z","validityPeriod":10,"nationalities":["KOR"]}]},{"issuingCountry":"LIE","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-10-26T00:00:00Z","validityPeriod":10,"nationalities":["LIE"]},{"type":"NationalIDCard","nationalities":["LIE"]},{"type":"ResidencePermit","nfcIssueDate":"2003-01-01T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"LUX","idDocuments":[{"type":"Passport","nfcIssueDate":"2015-02-16T00:00:00Z","validityPeriod":5,"nationalities":["LUX"]},{"type":"NationalIDCard","nfcIssueDate":"2014-07-01T00:00:00Z","validityPeriod":10,"nationalities":["LUX"]},{"type":"ResidencePermit","nfcIssueDate":"2011-05-20T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"MEX","idDocuments":[{"type":"Passport","nationalities":["MEX"]}]},{"issuingCountry":"MLT","idDocuments":[{"type":"Passport","nfcIssueDate":"2008-09-29T00:00:00Z","validityPeriod":10,"nationalities":["MLT"]},{"type":"NationalIDCard","nfcIssueDate":"2020-09-01T00:00:00Z","validityPeriod":10,"nationalities":["MLT"]}]},{"issuingCountry":"NLD","idDocuments":[{"type":"Passport","nfcIssueDate":"2014-03-09T00:00:00Z","validityPeriod":10,"nationalities":["NLD"]},{"type":"NationalIDCard","nfcIssueDate":"2014-03-09T00:00:00Z","validityPeriod":10,"nationalities":["NLD"]},{"type":"ResidencePermit","nfcIssueDate":"2012-04-01T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]},{"type":"DrivingLicense","nfcIssueDate":"2014-03-09T00:00:00Z","validityPeriod":10,"nationalities":["NLD"]}]},{"issuingCountry":"NOR","idDocuments":[{"type":"Passport","nfcIssueDate":"2005-10-01T00:00:00Z","validityPeriod":10,"nationalities":["NOR"]},{"type":"ResidencePermit","nfcIssueDate":"2012-06-04T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"PER","idDocuments":[{"type":"Passport","nfcIssueDate":"2016-07-08T00:00:00Z","validityPeriod":5,"nationalities":["PER"]}]},{"issuingCountry":"POL","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-01-01T00:00:00Z","validityPeriod":10,"nationalities":["POL"]},{"type":"NationalIDCard","nfcIssueDate":"2019-03-04T00:00:00Z","validityPeriod":10,"nationalities":["POL"]},{"type":"ResidencePermit","nfcIssueDate":"2011-08-23T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"PRT","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-08-28T00:00:00Z","validityPeriod":5,"nationalities":["PRT"]},{"type":"NationalIDCard","nfcIssueDate":"2019-03-04T00:00:00Z","nationalities":["PRT"]},{"type":"ResidencePermit","nfcIssueDate":"2008-12-22T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"ROU","idDocuments":[{"type":"Passport","nfcIssueDate":"2008-12-31T00:00:00Z","validityPeriod":5,"nationalities":["ROU"]}]},{"issuingCountry":"SGP","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-08-15T00:00:00Z","validityPeriod":10,"nationalities":["SGP"]}]},{"issuingCountry":"SVK","idDocuments":[{"type":"Passport","nfcIssueDate":"2008-01-15T00:00:00Z","validityPeriod":10,"nationalities":["SVK"]},{"type":"ResidencePermit","nfcIssueDate":"2011-09-19T00:00:00Z","validityPeriod":10,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"SVN","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-08-28T00:00:00Z","validityPeriod":10,"nationalities":["SVN"]},{"type":"NationalIDCard","nationalities":["SVN"]},{"type":"ResidencePermit","nfcIssueDate":"2011-05-18T00:00:00Z","validityPeriod":10,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"SWE","idDocuments":[{"type":"Passport","nfcIssueDate":"2012-01-02T00:00:00Z","validityPeriod":5,"nationalities":["SWE"]},{"type":"NationalIDCard","nfcIssueDate":"2012-01-02T00:00:00Z","validityPeriod":5,"nationalities":["SWE"]},{"type":"ResidencePermit","nfcIssueDate":"2011-05-20T00:00:00Z","validityPeriod":5,"nationalities":["ALB","ARG","AUS","AUT","BEL","BRA","CAN","CHE","CHL","CIV","DNK","DZA","ESP","EST","FIN","FRA","GBR","GRC","HUN","IND","IRL","ISL","ITA","LIE","LUX","MAR","MEX","NGA","NLD","NOR","POL","PRT","RUS","SGP","SVK","SVN","SWE","TUR","URY","USA","ZAF"]}]},{"issuingCountry":"TUR","idDocuments":[{"type":"Passport","nfcIssueDate":"2010-06-01T00:00:00Z","validityPeriod":10,"nationalities":["TUR"]}]},{"issuingCountry":"USA","idDocuments":[{"type":"Passport","nfcIssueDate":"2006-08-14T00:00:00Z","validityPeriod":10,"nationalities":["USA"]}]},{"issuingCountry":"ZAF","idDocuments":[{"type":"Passport","nationalities":["ZAF"]}]}]
        """

class OrcaSampleActivity : AppCompatActivity() {

    private val TAG: String = OrcaSampleActivity::class.java.simpleName

    private lateinit var binding: ActivityOrcaSampleBinding

    private val sampleDataSource: DataSource by lazy { LocalDataSource(
        CountryNetworkModel.create(SUPPORTED_COUNTRIES_AS_STRING)
    )}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrcaSampleBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupActionBar()
        setupButtons()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupButtons() = with(binding) {
        buttonTestMe.setOnClickListener { testOrca() }
        buttonDefaultFlow.setOnClickListener { buildDefaultFlow() }
        buttonCustomFlow.setOnClickListener { buildCustomFlow() }
        buttonStandaloneDocumentFlow.setOnClickListener { buildStandaloneDocumentFlow() }
    }

    private fun testOrca() {
        Orca.testMe(this)
    }

    private fun buildDefaultFlow() {
        val dataSource = sampleDataSource
        val kycInfo = KycResultContainer.kycInfo

        Orca.Builder(this)
            .withDefaultFlow()
            .addDataSource(dataSource)
            .addKycInfo(kycInfo)
            .present { finishReason ->
                if (finishReason is OrcaFinishReason.Completed) {
                    KycResultContainer.kycInfo = finishReason.kycInfo
                    Log.d(TAG, "Finished Orca with kyc info: ${finishReason.kycInfo}")
                }
            }
    }

    private fun buildCustomFlow() {
        val kycInfo = KycResultContainer.kycInfo

        Orca.Builder(this)
            .withCustomFlow()
            .addSelfieFlow()
            .addLocationFlow()
            .noMoreFlows()
            .addKycInfo(kycInfo)
            .present { finishReason ->
                if (finishReason is OrcaFinishReason.Completed) {
                    KycResultContainer.kycInfo = finishReason.kycInfo
                    Log.d(TAG, "Finished Orca with kyc info: ${finishReason.kycInfo}")
                }
            }
    }

    private fun buildStandaloneDocumentFlow() {
        val config = OrcaStandaloneDocumentConfig(type = DocumentType.PASSPORT)
        val dataSource = sampleDataSource
        val kycInfo = KycResultContainer.kycInfo

        Orca.Builder(this)
            .withStandaloneDocumentScanner(config)
            .addDataSource(dataSource)
            .addKycInfo(kycInfo)
            .present { finishReason ->
                if (finishReason is OrcaFinishReason.Completed) {
                    KycResultContainer.kycInfo = finishReason.kycInfo
                    Log.d(TAG, "Finished Orca with kyc info: ${finishReason.kycInfo}")
                }
            }
    }
}