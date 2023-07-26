package com.domain.BaseModels

import com.google.gson.annotations.SerializedName

data class NewInterviewDetails (
    @SerializedName("InterviewId") val interviewId : Int,
    @SerializedName("InterviewDateTime") val interviewDateTime : String,
    @SerializedName("InterviewTitle") val interviewTitle : String,
    @SerializedName("ClientName") val clientName : String,
    @SerializedName("InterviewTimezone") val interviewTimezone : String,
    @SerializedName("Sid") val sid : String,
    @SerializedName("RecruiterId") val recruiterId : Int,
    @SerializedName("SaveProfileId") val saveProfileId : Int,
    @SerializedName("IsInterviewCancelled") val isInterviewCancelled : String,
    @SerializedName("CancelltionDescription") val cancelltionDescription : String,
    @SerializedName("IsVideoRecordEnabled") var isVideoRecordEnabled : Boolean,
    @SerializedName("GoogleCalendarSyncEnabled") val googleCalendarSyncEnabled : Boolean,
    @SerializedName("OutlookCalendarSyncEnabled") val outlookCalendarSyncEnabled : Boolean,
    @SerializedName("AllowToMute") val allowToMute : Boolean,
    @SerializedName("CandidateId") val candidateId : Int,
    @SerializedName("Status") val status : String,
    @SerializedName("Duration") val duration : String,
    @SerializedName("interviewDuration") val interviewDuration : Int,
    @SerializedName("subscriberid") val subscriberid : Int,
    @SerializedName("jobid") val jobid : String,
    @SerializedName("RecruiterSearch") val recruiterSearch : String,
    @SerializedName("Days_to_Hire") val days_to_Hire : String,
    @SerializedName("Interviewers") val interviewers : String,
    @SerializedName("CandidateFirstName") val candidateFirstName : String,
    @SerializedName("CandidateLastName") val candidateLastName : String,
    @SerializedName("TotalCount") val totalCount : Int,
    @SerializedName("EmailID") val emailID : String,
    @SerializedName("ContactNumber") val contactNumber : String,
    @SerializedName("EmailAcceptanceStatus") val emailAcceptanceStatus : Int,
    @SerializedName("InterviewerList") val interviewerList : List<String>,
    @SerializedName("VideoDuration") val videoDuration : Int,
    @SerializedName("MSMeetingMode") val mSMeetingMode : String,
    @SerializedName("VideoCallAccessCode") val VideoCallAccessCode : String="",
    @SerializedName("MSMeetingUrl") val msMeetingUrl : String=""
)
data class tempclass(val name:String)