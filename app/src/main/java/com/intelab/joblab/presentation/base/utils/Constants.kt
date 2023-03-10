@file:Suppress("ObjectPropertyName")

package com.intelab.joblab.presentation.base.utils

import com.intelab.joblab.R
import com.intelab.joblab.domain.entities.ComplementaryRegister
import com.intelab.joblab.domain.entities.HomeStatusResponse
import com.intelab.joblab.domain.entities.ServiceUI
import com.intelab.joblab.domain.entities.SpinnerItemUI
import com.intelab.joblab.domain.entities.requests.Services
import com.intelab.joblab.presentation.JoblabApplication
import com.intelab.joblab.presentation.extensions.replaceDoubleSpace
import com.intelab.joblab.presentation.extensions.upperCaseDefault
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getAcademicStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getAccutestStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getConfirmStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getCurrentlyStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getDayStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getDomicileStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getEconomicStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getFinancialStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getJobReferencesStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getLifeStyleStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getMonthStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getSocialNetworkStr
import com.intelab.joblab.presentation.base.utils.Constants.Companion.getYearStr
import java.text.DecimalFormat

/**
 * values that do not have a translation and must be used as is throughout the App.
 * */
const val _patternDecimal = "00"
const val _accutestTest = "TEST"
const val _accutestStepOne = "STEP1"
const val _accutestStepTwo = "STEP2"
const val _imageUpper = "IMAGE"
const val _document = "DOCUMENT"
const val _imageLower = "image"
const val _imageKey = "imageKey"
const val _answers = "answers"
const val _data = "data"
const val _type = "type"
const val _noEmail = "no email"
const val _imagePickerType = "image/*"
const val _imageJpgType = "image/jpg"
const val _allFilesType = "*/*"
const val _appPdfType = "application/pdf"
const val _appJsonType = "application/json"
const val _fileType = "file"
const val _fileNameType = "filename"
const val _package = "package"
const val _fivePlus = "5+"
const val _pdf = "pdf"
const val _email = "email"
const val _joblabApp = "JoblabApp"
const val _firebase = "Firebase"
const val _joblabTopicName = "joblab-complete-profile"
const val _monthYearPickerDialogTag = "MonthYearPickerDialog"
const val _validationCode = "validationCode"
const val _foreign = "Extranjero"
const val _national = "Nacional"
const val _smallSizeTech = "=s100"
const val _connectivityAction = "android.net.conn.CONNECTIVITY_CHANGE"
const val JOB_DATABASE_ID = "referenceJobId"
const val OPEN_NOTIFICATIONS = "OPEN_NOTIFICATIONS"
const val OPEN_ACCUTEST = "OPEN_ACCUTEST"
const val RECRUITER_PROFILE = "RECRUITER"
const val USER_ACTIVATE_STATE = "ACTIVATE"
const val USER_NOT_EXIST_STATE = "NOT_EXIST"
const val USER_INITIAL_REGISTER_STATE = "INITIAL_REGISTER"
const val USER_COMPLEMENTARY_REGISTER_STATE = "COMPLEMENTARY_REGISTER"
const val USER_COMPLETED_STATE = "COMPLETED"
const val NOT_EXIST = "NOT_EXIST"
const val HUAWEI = "HUAWEI"
const val _notificationTitle = "title"
const val _notificationMessage = "message"
const val _notificationAction = "action"

/**
 * messageKey values for Accutest Errors
 * */
const val ERROR_ACCUTEST_INELEGIBLE = "ERROR_ACCUTEST_INELEGIBLE"
const val ERROR_VALIDATING_ACCUTEST = "ERROR_VALIDATING_ACCUTEST"
const val ERROR_VALIDATING_ACCUTEST_REMAINING_ZERO = "ERROR_VALIDATING_ACCUTEST_REMAINING_ZERO"
const val ERROR_VALIDATING_ACCUTEST_REMAINING = "ERROR_VALIDATING_ACCUTEST_REMAINING"

/**
 * Values as Integers, Floats or Longs
 * */
const val _indexOneNegative = -1
const val _indexZero = 0
const val _indexOne = 1
const val _indexTwo = 2
const val _indexThree = 3
const val _indexFour = 4
const val _indexFive = 5
const val _indexSix = 6
const val _indexSeven = 7
const val _indexEight = 8
const val _indexNine = 9
const val _indexTen = 10
const val _indexEleven = 11
const val _indexFourteen = 14
const val _indexEighteen = 18
const val _indexTwentyFive = 25
const val _percentageCompleted = 100
const val _minYear = 1960
const val _mxCountryId = _indexOne
const val _maxEndMonth = _indexEleven
const val _minCurpLengthForeign = _indexOne
const val _minCurpLengthNational = _indexEighteen
const val _maxCurpLengthForeign = _indexTwentyFive
const val _maxCurpLengthNational = _indexEighteen
const val _delay50 = 500L
const val _delay200 = 2000L
const val _delay250 = 2500L
const val _delay10 = 100L
const val _delay100 = 1000L
const val _delay20 = 200L
const val _maxFileSize = 3_145_728L
const val _rotationDegrees = 180f
const val _maxFileSizeAllowed = 9999999

/**
 * values composed of two Integers that result in a String
 * */
const val _profileJobsNo = "$_indexOne/$_indexNine"
const val _profilePersonalNo = "$_indexTwo/$_indexNine"
const val _profileDomicileNo = "$_indexThree/$_indexNine"
const val _profileCreditNo = "$_indexFour/$_indexNine"
const val _profileLifeStyleNo = "$_indexFive/$_indexNine"
const val _profileEconomicNo = "$_indexSix/$_indexNine"
const val _profileAcademicNo = "$_indexSeven/$_indexNine"
const val _profileAddJobNo = "$_indexEight/$_indexNine"
const val _profileSocialNo = "$_indexNine/$_indexNine"

/**
 * values Integers converted to Strings
 * */
const val _zeroAsStr = _indexZero.toString()
const val _oneAsStr = _indexOne.toString()
const val _twoAsStr = _indexTwo.toString()
const val _threeAsStr = _indexThree.toString()
const val _fourAdStr = _indexFour.toString()
const val _fiveAsStr = _indexFive.toString()
const val _sixAsStr = _indexSix.toString()
const val _fourteenAsStr = _indexFourteen.toString()

/**
 * values that do have a translation and must be taken from string resources
 * */
val _accutestTitle = getAccutestStr()
val _yearStr = getYearStr()
val _monthStr = getMonthStr()
val _dayStr = getDayStr()
val _domicile = getDomicileStr()
val _jobReferences = getJobReferencesStr()
val _lifeStyle = getLifeStyleStr()
val _financialInformation = getFinancialStr()
val _socialNetworks = getSocialNetworkStr()
val _academic = getAcademicStr()
val _economic = getEconomicStr()
val _confirm = getConfirmStr()
val NOW = getCurrentlyStr()

/**
 * multiple functions that are used throughout the App and are repetitive.
 * */
class Constants {
    companion object {
        private val context = JoblabApplication.appContext

        fun getAccutestStr() = context.getString(R.string.txt_accutest).upperCaseDefault()
        fun getYearStr() = context.getString(R.string.txt_year)
        fun getMonthStr() = context.getString(R.string.txt_month)
        fun getDayStr() = context.getString(R.string.txt_day)

        fun getDomicileStr() = context.getString(R.string.tv_localization)
        fun getJobReferencesStr() = context.getString(R.string.tv_job_references)
        fun getLifeStyleStr() = context.getString(R.string.tv_life_style)
        fun getFinancialStr() = context.getString(R.string.tv_financial_information)
        fun getSocialNetworkStr() = context.getString(R.string.tv_social_media)
        fun getAcademicStr() = context.getString(R.string.tv_academic_title)
        fun getEconomicStr() = context.getString(R.string.tv_economic)
        fun getConfirmStr() = context.getString(R.string.txt_confirm).upperCaseDefault()
        fun getCurrentlyStr() = context.getString(R.string.txt_currently)

        fun getYearList(actualYear: Int) = (_minYear..actualYear - _indexEighteen).map { SpinnerItemUI(it.toString()) }.reversed().toMutableList()
        fun getChildrenNumber() = (0..5).map { SpinnerItemUI("$it") }.toMutableList()
        fun getPeopleAtHome() = (1..5).map { SpinnerItemUI("$it") }.toMutableList()
        fun getCreditCards() = (0..5).map { SpinnerItemUI("$it") }.toMutableList()
        fun getDependents() = (0..5).map { SpinnerItemUI("$it") }.toMutableList()
        fun getOwnCars() = (0..5).map { SpinnerItemUI("$it") }.toMutableList()
        fun getMonthList(yearValue: Int, actualYear: Int, actualDay: Int, actualMonth: Int) : MutableList<SpinnerItemUI> {
            return if (yearValue == actualYear) {
                if (actualDay == 1) (1..actualMonth) else (1..actualMonth + 1)
            } else {
                if (yearValue == actualYear - 18) (1..actualMonth + 1) else (1..12)
            }.map { SpinnerItemUI(DecimalFormat(_patternDecimal).format(it)) }.toMutableList()
        }
        fun getSpinner(value: String) = SpinnerItemUI(value)
        fun getProgressFormula(progress: Int) = progress / 8f * 100
        fun getFullName(cr: HomeStatusResponse) : String {
            val firstName = cr.firstName ?: ""
            val middleName = cr.middleName ?: ""
            val surnamePaternal = cr.surnamePaternal ?: ""
            val surnameMaternal = cr.surnameMaternal ?: ""
            return "$firstName $middleName $surnamePaternal $surnameMaternal".replaceDoubleSpace()
        }
        fun getFullName(cr: ComplementaryRegister) : String {
            val firstName = cr.firstName ?: ""
            val middleName = cr.otherNames ?: ""
            val surnamePaternal = cr.fatherLastName ?: ""
            val surnameMaternal = cr.motherLastName ?: ""
            return "$firstName $middleName $surnamePaternal $surnameMaternal".replaceDoubleSpace()
        }
        fun getServiceUI(it: Services) = ServiceUI(it.id?.toInt() ?: 0, it.description ?: "")
        fun getSpinnerUI(it: String) = SpinnerItemUI(text = if (it == _sixAsStr) _fivePlus else it)
        fun validateThreeConditionsInt(value: Int?): Boolean? {
            return when(value) {
                1 -> true
                2 -> false
                else -> null
            }
        }
        fun validateThreeConditionsBool(value: Boolean?): Int {
            return when(value) {
                true -> 1
                false -> 2
                else -> 0
            }
        }
        fun getMonthsAsStringList() = with(context) {
            arrayOf(
                resources.getString(R.string.txt_january),
                resources.getString(R.string.txt_february),
                resources.getString(R.string.txt_march),
                resources.getString(R.string.txt_april),
                resources.getString(R.string.txt_may),
                resources.getString(R.string.txt_june),
                resources.getString(R.string.txt_july),
                resources.getString(R.string.txt_august),
                resources.getString(R.string.txt_september),
                resources.getString(R.string.txt_october),
                resources.getString(R.string.txt_november),
                resources.getString(R.string.txt_december)
            )
        }
    }
}

/**
 * types of file names send it to Server as multipart.
 * */
enum class FileNames(val value: String) {
    CANDIDATE("candidate-photo"),
    ACCUTEST("accutest-photo"),
    TESTING("testing")
}

/**
 * services used on registration and profile modules.
 * */
enum class ServicesIds(val value: Int) {
    WATER(1),
    LIGHT(2),
    PHONE(3),
    TV(4),
    GAS(5),
    INTERNET(6)
}