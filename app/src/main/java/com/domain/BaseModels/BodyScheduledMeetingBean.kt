package com.domain.BaseModels
import com.google.gson.annotations.SerializedName


data class BodyScheduledMeetingBean (
	@SerializedName("Recruiter") val Recruiter : String?=null,
	@SerializedName("Subscriber") val Subscriber : String?=null,
	@SerializedName("PageNumber") val PageNumber : Int=1,
	@SerializedName("PageSize") val PageSize : Int=9,
	@SerializedName("Search") val Search : String="",
	@SerializedName("fromdate") val fromdate : String?=null,
	@SerializedName("todate") val todate : String?=null,
	@SerializedName("from") val from : String?=null,
	@SerializedName("to") val to : String?=null,
	@SerializedName("Client") val Client : String="",
	@SerializedName("Status") val Status : String="schedule",
	@SerializedName("CName") val CName : String="",
	@SerializedName("Day") val Day : String="",
	@SerializedName("Time") val Time : String="",
	@SerializedName("IName") val IName : String="",
	@SerializedName("IName1") val IName1 : String="",
	@SerializedName("IName2") val IName2 : String="",
	@SerializedName("IName3") val IName3 : String="",
	@SerializedName("IName4") val IName4 : String="",
	@SerializedName("IName5") val IName5 : String="",
	@SerializedName("IName6") val IName6 : String="",
	@SerializedName("Counter") val Counter : Int=0,
	@SerializedName("Recruitervalue") val Recruitervalue : String="",
	@SerializedName("country") val country : String="",
	@SerializedName("state") val state : String="",
	@SerializedName("city") val city : String="",
	@SerializedName("IsDataForDownload") val IsDataForDownload : Boolean=false,
	@SerializedName("RecruiterSearch") val RecruiterSearch : String="",
	@SerializedName("Isweb") val Isweb : Boolean=false,
	@SerializedName("RecruiterEmail") val RecruiterEmail : String="",

)


