package com.domain.BaseModels
import com.google.gson.annotations.SerializedName


data class ResponseInterviewUserDetails (

	@SerializedName("VideoAccessCode") val videoAccessCode : String,
	@SerializedName("InterviewId") val interviewId : Int,
	@SerializedName("Status") val status : String,
	@SerializedName("InterviewTitle") val interviewTitle : String,
	@SerializedName("ClientName") val clientName : String,
	@SerializedName("Sid") val sid : String,
	@SerializedName("RecruiterId") val recruiterId : String,
	@SerializedName("InterviewTimezone") val interviewTimezone : String,
	@SerializedName("IsInterviewCancelled") val isInterviewCancelled : String,
	@SerializedName("TotalInterviewerCount") val totalInterviewerCount : Int,
	@SerializedName("TotalParticipantCount") val totalParticipantCount : Int,
	@SerializedName("InterviewerList") val interviewerList : List<InterviewerList>,
	@SerializedName("APIResponse") val aPIResponse : APIResponse,
	@SerializedName("AllowToMute") val allowToMute : Boolean,
	@SerializedName("ScreenShareCurrentStatus") val screenShareCurrentStatus : Boolean,
	@SerializedName("MeetingPassword") val meetingPassword : String,
	@SerializedName("EventType") val eventType : String,
	@SerializedName("Message") val message : String,
	@SerializedName("StatusCode") val statusCode : String,
	@SerializedName("Success") val success : Boolean
)