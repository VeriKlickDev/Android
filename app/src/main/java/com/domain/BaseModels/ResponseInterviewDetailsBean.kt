package com.domain.BaseModels
import com.google.gson.annotations.SerializedName



data class APIResponse (

	@SerializedName("Message") val message : String?=null,
	@SerializedName("StatusCode") val statusCode : Int?=null,
	@SerializedName("Success") val success : Boolean?=null
)



data class Candidate (

	@SerializedName("firstName") val firstName : String?=null,
	@SerializedName("lastName") val lastName : String?=null,
	@SerializedName("emailId") val emailId : String?=null,
	@SerializedName("contactNumber") val contactNumber : String?=null,
	@SerializedName("Countrycode") val countrycode : Int?=null,
	@SerializedName("UserProfileURL") val userProfileURL : String?=null,
	@SerializedName("videoAccessCode") val videoAccessCode : String?=null,
	@SerializedName("videoAccessURL") val videoAccessURL : String?=null,
	@SerializedName("MSTeamsvideoAccessURL") val mSTeamsvideoAccessURL : String?=null,
	@SerializedName("IsPresenter") val isPresenter : Boolean?=null,
	@SerializedName("licenseFrontImagePath") val licenseFrontImagePath : String?=null,
	@SerializedName("InterviewerTimezone") val interviewerTimezone : String?=null,
	@SerializedName("EmailAcceptanceStatus") val emailAcceptanceStatus : Int?=null,
	@SerializedName("InterviewerId") val interviewerId : Int?=null,
	@SerializedName("ResumePath") val ResumePath : String?=null
)



data class InterviewerList (

	@SerializedName("firstName") val firstName : String,
	@SerializedName("lastName") val lastName : String,
	@SerializedName("emailId") val emailId : String,
	@SerializedName("contactNumber") val contactNumber : String,
	@SerializedName("Countrycode") val countrycode : Int,
	@SerializedName("UserProfileURL") val userProfileURL : String,
	@SerializedName("videoAccessCode") val videoAccessCode : String,
	@SerializedName("videoAccessURL") val videoAccessURL : String,
	@SerializedName("MSTeamsvideoAccessURL") val mSTeamsvideoAccessURL : String,
	@SerializedName("IsPresenter") val isPresenter : Boolean,
	@SerializedName("licenseFrontImagePath") val licenseFrontImagePath : String,
	@SerializedName("InterviewerTimezone") val interviewerTimezone : String,
	@SerializedName("EmailAcceptanceStatus") val emailAcceptanceStatus : Int,
	@SerializedName("InterviewerId") val interviewerId : Int
)



data class InterviewModel (

	@SerializedName("InterviewId") val interviewId : Int?=null,
	@SerializedName("InterviewDateTime") val interviewDateTime : String?=null,
	@SerializedName("Status") val status : String?=null,
	@SerializedName("InterviewTitle") val interviewTitle : String?=null,
	@SerializedName("ClientName") val clientName : String?=null,
	@SerializedName("Sid") val sid : String?=null,
	@SerializedName("SaveProfileId") val saveProfileId : Int?=null,
	@SerializedName("RecruiterId") val recruiterId : Int?=null,
	@SerializedName("InterviewTimezone") val interviewTimezone : String?=null,
	@SerializedName("IsInterviewCancelled") val isInterviewCancelled : String?=null,
	@SerializedName("CancelltionDescription") val cancelltionDescription : String?=null,
	@SerializedName("IsVideoRecordEnabled") val isVideoRecordEnabled : Boolean?=null,
	@SerializedName("GoogleCalendarSyncEnabled") val googleCalendarSyncEnabled : Boolean?=null,
	@SerializedName("OutlookCalendarSyncEnabled") val outlookCalendarSyncEnabled : Boolean?=null,
	@SerializedName("AllowToMute") val allowToMute : Boolean?=null,
	@SerializedName("Candidate") val candidate : Candidate?=null,
	@SerializedName("InterviewerList") val interviewerList : List<InterviewerList>?=null,
	@SerializedName("EventType") val eventType : String?=null,
	@SerializedName("APIResponse") val aPIResponse : String?=null,
	@SerializedName("CandidateId") val candidateId : Int?=null,
	@SerializedName("Password") val password : String?=null,
	@SerializedName("subscriberid") val subscriberid : String?=null,
	@SerializedName("selected") val selected : Boolean?=null,
	@SerializedName("Duration") val duration : String?=null,
	@SerializedName("interviewDuration") val interviewDuration : Int?=null,
	@SerializedName("CandidateTimezone") val candidateTimezone : String?=null,
	@SerializedName("type") val type : String?=null,
	@SerializedName("jobid") val jobid : String?=null,
	@SerializedName("RecruiterSearch") val recruiterSearch : String?=null,
	@SerializedName("logedInUser") val logedInUser : String?=null,
	@SerializedName("Job_ID_created_on") val job_ID_created_on : String?=null,
	@SerializedName("Days_to_Hire") val days_to_Hire : Int?=null,
	@SerializedName("JobDetailsFieldsList") val jobDetailsFieldsList : String?=null,
	@SerializedName("SEQUENCE_ID") val sEQUENCE_ID : Int?=null,
	@SerializedName("Interviewer_video_accesscode") val interviewer_video_accesscode : String?=null,
	@SerializedName("Interviewer_Name") val interviewer_Name : String?=null,
	@SerializedName("AssessmentExpiryDate") val assessmentExpiryDate : String?=null,
	@SerializedName("MsteamToken") val msteamToken : String?=null,
	@SerializedName("MeetingMode") val meetingMode : String?=null,
	@SerializedName("MSMeetingMode") val mSMeetingMode : String?=null,
	@SerializedName("MSMeetingUrl") val mSMeetingUrl : String?=null,
	@SerializedName("MSTollNumber") val mSTollNumber : String?=null,
	@SerializedName("MSDialingurl") val mSDialingurl : String?=null,
	@SerializedName("VoiceCallPassCode") val voiceCallPassCode : String?=null,
	@SerializedName("VoiceCallPhoneNumber") val voiceCallPhoneNumber : String?=null
)



data class ResponseInterViewDetailsBean (

	@SerializedName("UserType") val userType : String?=null,
    @SerializedName("ChatChannel") val chatChannel : String?=null,
    @SerializedName("ChannelCreatedBy") val channelCreatedBy : String?=null,
    @SerializedName("ChatUserIdentity") val chatUserIdentity : String?=null,
    @SerializedName("Identity") val identity : String?=null,
    @SerializedName("VideoAccessCode") var videoAccessCode : String?=null,
    @SerializedName("TimeDiff") val timeDiff : Double?=null,
    @SerializedName("InterviewerFirstName") val interviewerFirstName : String?=null,
    @SerializedName("InterviewerLastName") val interviewerLastName : String?=null,
    @SerializedName("IsPresenter") val isPresenter : Boolean?=null,
    @SerializedName("IsVideoRecordEnabled") val isVideoRecordEnabled : Boolean?=null,
    @SerializedName("GoogleCalendarSyncEnabled") val googleCalendarSyncEnabled : Boolean?=null,
    @SerializedName("OutlookCalendarSyncEnabled") val outlookCalendarSyncEnabled : Boolean?=null,
    @SerializedName("AllowToMute") val allowToMute : Boolean?=null,
    @SerializedName("Users") val users : List<Users>?=null,
    @SerializedName("ScheduledDateTime") val scheduledDateTime : String?=null,
    @SerializedName("InterviewModel") val interviewModel : InterviewModel?=null,
    @SerializedName("InterviewTimezone") val interviewTimezone : String?=null,
    @SerializedName("APIResponse") val aPIResponse : APIResponse?=null,
    @SerializedName("VoiceCallPassCode") val voiceCallPassCode : String?=null,
    @SerializedName("VoiceCallPhoneNumber") val voiceCallPhoneNumber : String?=null
)



data class Users (

	@SerializedName("UserSeqId") val userSeqId : Int,
	@SerializedName("InterviewSeqId") val interviewSeqId : Int,
	@SerializedName("UserFirstName") val userFirstName : String,
	@SerializedName("UserLastName") val userLastName : String,
	@SerializedName("UserEmailId") val userEmailId : String,
	@SerializedName("UserContactNumber") val userContactNumber : String,
	@SerializedName("UserProfileURL") val userProfileURL : String,
	@SerializedName("UserType") val userType : String,
	@SerializedName("VideoCallAccessCode") val videoCallAccessCode : String,
	@SerializedName("EntryDateTime") val entryDateTime : String,
	@SerializedName("interview") val interview : String,
	@SerializedName("IsPresenter") val isPresenter : Boolean,
	@SerializedName("Status") val status : String,
	@SerializedName("ScreenShareCurrentStatus") val screenShareCurrentStatus : Boolean,
	@SerializedName("InterviewerTimezone") val interviewerTimezone : String,
	@SerializedName("EmailAcceptanceStatus") val emailAcceptanceStatus : Int,
	@SerializedName("Id") val id : Int,
	@SerializedName("Name") val name : String,
	@SerializedName("ResumePath") val ResumePath : String
)


