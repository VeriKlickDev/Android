package com.data.dataHolders

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private var day = 0

private var dateTxtIst = ""
private var dateTxtUtc = ""

object WeeksDataHolder {

    fun minusWeekDay() {
        day = day - 7
    }

    fun addWeekDay() {
        day = day + 7
    }

    fun getCurrentDaysCount() = day

    fun setDayToZero() {
        day = 0
    }

    fun minus7Days() {
        day = day - 7
    }


    val cal = Calendar.getInstance()

    fun setCalendarInstance() {


        cal.firstDayOfWeek = Calendar.DAY_OF_MONTH
        //cal.add(Calendar.DATE, getCurrentDaysCount())

        //cal.set(Calendar.DAY_OF_MONTH)
      //  day = cal.get(Calendar.DATE)
        Log.d("TAG", ": init func day $day")
    }

    private fun getCalculatedDateofWeek(dateFormat: String?, days: Int, action: Int): String? {

        // day=cal.get(Calendar.DAY_OF_YEAR)

        when (action) {
            1 -> {

                 cal.set(Calendar.HOUR_OF_DAY,11)
                 cal.set(Calendar.MINUTE,59)
            }
            2 -> {
                 cal.set(Calendar.HOUR_OF_DAY,12)
                 cal.set(Calendar.MINUTE,0)
            }
        }

        //Log.d("TAG", "getCalculatedDateofWeek: day $day")

        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)

        s.format(Date(cal.timeInMillis))

        return s.format(Date(cal.timeInMillis))
    }

    private var istDate:String?=null
    private var utcDate:String?=null

    fun getNextISTandUTCDate(date:String,result: (ist: String, utc: String) -> Unit) {
        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,11)
        cal.set(Calendar.MINUTE,59)

        cal.add(Calendar.DAY_OF_YEAR, 7)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )
    }


    fun getIncreasedDate(date:String, result: (ist: String, utc: String) -> Unit) {

        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,12)
        cal.set(Calendar.MINUTE,0)

        cal.add(Calendar.DAY_OF_YEAR, 1)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )
    }

    fun getDecreasedDate(date:String, result: (ist: String, utc: String) -> Unit) {

        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,12)
        cal.set(Calendar.MINUTE,0)

        cal.add(Calendar.DAY_OF_YEAR, -1)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )
    }


    fun getPastISTandUTCDate(date:String, result: (ist: String, utc: String) -> Unit) {

        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,12)
        cal.set(Calendar.MINUTE,0)

        cal.add(Calendar.DAY_OF_YEAR, -7)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )
    }


    fun getCurrentNextDayISTandUTCDate(date:String,result: (ist: String, utc: String) -> Unit) {
        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,12)
        cal.set(Calendar.MINUTE,0)

        cal.add(Calendar.DAY_OF_YEAR, 0)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )
    }



    fun getCurrentPreviousDayISTandUTCDate(date:String,result: (ist: String, utc: String) -> Unit) {
        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,12)
        cal.set(Calendar.MINUTE,0)

        cal.add(Calendar.DAY_OF_YEAR, 0)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )
    }



    fun convertTo1159(date:String,result: (ist: String, utc: String) -> Unit)
    {
        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,11)
        cal.set(Calendar.MINUTE,59)

        cal.add(Calendar.DAY_OF_YEAR, 0)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )

    }

    fun convertTo1200(date:String,result: (ist: String, utc: String) -> Unit)
    {
        val cal = Calendar.getInstance()
        val sdf=SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date=sdf.parse(date)

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.time=date

        cal.set(Calendar.HOUR_OF_DAY,12)
        cal.set(Calendar.MINUTE,0)

        cal.add(Calendar.DAY_OF_YEAR, 0)

        istDate= sdfIST.format(cal.time)
        utcDate=sdfUTC.format(cal.time)

        result(
            istDate !!,
            utcDate!!
        )

    }












    data class DateHolderModel(val preDate:String,val nextDate:String)
    private val list= mutableListOf<DateHolderModel>()
    fun setDate(preDate:String,nextDate:String)
    {
        list.add(0,DateHolderModel(preDate,nextDate))
    }
    fun getDate()=list.firstOrNull()


  /*  fun getCurrentPastISTandUTCDate(result: (ist: String, utc: String) -> Unit) {
        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        cal.add(Calendar.DAY_OF_YEAR,0)

        istDate= sdfIST.format(Date(cal.timeInMillis))
        utcDate=sdfUTC.format(Date(cal.timeInMillis))

        // addWeekDay()
        result(
            istDate !!,
            utcDate!!
        )
    }*/


    private val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")


    fun getIstUtcPriviousDate(dateResponse: (utcDate: String, istDate: String) -> Unit) {


        val calendar = Calendar.getInstance()

        calendar.firstDayOfWeek = Calendar.DAY_OF_MONTH
        calendar.add(Calendar.DATE, getCurrentDaysCount())

        var datetimeItc: String = sdfIST.format(calendar.time)
        var datetimeUtc: String = sdfUTC.format(calendar.time)

        dateResponse(datetimeUtc, datetimeItc)

        dateTxtIst = datetimeItc
        dateTxtUtc = datetimeUtc
    }


    fun getItcUtcNextDate(dateResponse: (utcDate: String, istDate: String) -> Unit) {

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val calendar = Calendar.getInstance()

        calendar.firstDayOfWeek = Calendar.DAY_OF_MONTH
        calendar.add(Calendar.DATE, getCurrentDaysCount())

        var datetimeIst: String = sdfIST.format(calendar.time)
        var datetimeUtc: String = sdfUTC.format(calendar.time)

        dateResponse(datetimeUtc, datetimeIst)

        dateTxtIst = datetimeIst
        dateTxtUtc = datetimeUtc
    }


    fun getCalculatedDate(days: Int): String? {

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        val cal = Calendar.getInstance()

        cal.add(Calendar.DAY_OF_YEAR, days)
        //cal.add(Calendar.DAY_OF_YEAR)
        return sdfIST.format(Date(cal.timeInMillis))
    }


    fun getCalculatedDate(date: String?, days: Int): String? {
        val cal = Calendar.getInstance()
        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        cal.add(Calendar.DAY_OF_YEAR, days)
        try {
            return sdfIST.format(Date(sdfIST.parse(date).time))
        } catch (e: ParseException) {
            // TODO Auto-generated catch block
            Log.e("TAG", "Error in Parsing Date : " + e.message)
        }
        return null
    }


    fun getItcUtcNextDate(
        datestr: String,
        dateResponse: (utcDate: String, istDate: String) -> Unit
    ) {

        val sdfIST = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val sdfUTC = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        val calendar = Calendar.getInstance()

        val date = sdfIST.parse(datestr)

        val cal = sdfIST.format(date)

        val dayofdate = cal.format(Calendar.DAY_OF_MONTH) + 7
        val month = cal.format(Calendar.MONTH)
        val year = cal.format(Calendar.YEAR)
        val hours = cal.format(Calendar.HOUR_OF_DAY)
        val minuts = cal.format(Calendar.MINUTE)
        val seconds = cal.format(Calendar.SECOND)

        val finalDate =
            year + "-" + month + "-" + dayofdate + " " + hours + ":" + minuts + ":" + seconds
        dateResponse("datetimeUtc", finalDate)
        /* calendar.firstDayOfWeek= Calendar.DAY_OF_MONTH
         calendar.add(Calendar.DATE, getCurrentDaysCount())

         var datetimeIst: String = sdfIST.format(calendar.time)
         var datetimeUtc: String = sdfUTC.format(calendar.time)
         dateResponse(datetimeUtc,finalDate)

         dateTxtIst=datetimeIst
         dateTxtUtc= datetimeUtc*/
    }


    private val formatter = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

    fun toCalendar(date: Date): Calendar {
        val cal: Calendar = Calendar.getInstance(TimeZone.getDefault())
        cal.time = date
        return cal
    }

    fun printRangeAfterWeek(date: Date, rangeType: Int): List<String> {
        val cal: Calendar = toCalendar(date)

        val startDay = cal.getActualMinimum(rangeType)
        cal.set(rangeType, startDay)
        val startDate = cal.time

        val endDay = cal.getActualMaximum(rangeType)
        cal.set(rangeType, endDay)
        val endDate = cal.time

        println("${formatter.format(startDate)} to ${formatter.format(endDate)}")
        val list = mutableListOf<String>()
        list.add(0, startDay.toString())
        list.add(1, endDay.toString())

        return list
    }


    fun printRangeBeforeWeek(date: Date, rangeType: Int) {
        val cal: Calendar = toCalendar(date)

        val startDay = cal.getActualMinimum(rangeType)
        cal.set(rangeType, startDay)
        val startDate = cal.time

        val endDay = cal.getActualMaximum(rangeType)
        cal.set(rangeType, endDay)
        val endDate = cal.time

        println("${formatter.format(startDate)} to ${formatter.format(endDate)}")
    }


}