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

    fun minus7Days()
    {
        day=day-7
    }

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

    fun printRangeAfterWeek(date: Date, rangeType: Int) : List<String> {
        val cal: Calendar = toCalendar(date)

        val startDay = cal.getActualMinimum(rangeType)
        cal.set(rangeType, startDay)
        val startDate = cal.time

        val endDay = cal.getActualMaximum(rangeType)
        cal.set(rangeType, endDay)
        val endDate = cal.time

        println("${formatter.format(startDate)} to ${formatter.format(endDate)}")
        val list= mutableListOf<String>()
        list.add(0,startDay.toString())
        list.add(1,endDay.toString())

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