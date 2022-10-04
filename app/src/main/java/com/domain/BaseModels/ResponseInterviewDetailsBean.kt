package com.domain.BaseModels
import com.google.gson.annotations.SerializedName



data class APIResponse (

	@SerializedName("Message") val message : String?=null,
	@SerializedName("StatusCode") val statusCode : Int?=null,
	@SerializedName("Success") val success : Boolean?=null
)



data class Candidate (

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
	@SerializedName("InterviewerId") val interviewerId : Int,
	@SerializedName("ResumePath") val ResumePath : String
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

	@SerializedName("InterviewId") val interviewId : Int,
	@SerializedName("InterviewDateTime") val interviewDateTime : String,
	@SerializedName("Status") val status : String,
	@SerializedName("InterviewTitle") val interviewTitle : String,
	@SerializedName("ClientName") val clientName : String,
	@SerializedName("Sid") val sid : String,
	@SerializedName("SaveProfileId") val saveProfileId : Int,
	@SerializedName("RecruiterId") val recruiterId : Int,
	@SerializedName("InterviewTimezone") val interviewTimezone : String,
	@SerializedName("IsInterviewCancelled") val isInterviewCancelled : String,
	@SerializedName("CancelltionDescription") val cancelltionDescription : String,
	@SerializedName("IsVideoRecordEnabled") val isVideoRecordEnabled : Boolean,
	@SerializedName("GoogleCalendarSyncEnabled") val googleCalendarSyncEnabled : Boolean,
	@SerializedName("OutlookCalendarSyncEnabled") val outlookCalendarSyncEnabled : Boolean,
	@SerializedName("AllowToMute") val allowToMute : Boolean,
	@SerializedName("Candidate") val candidate : Candidate,
	@SerializedName("InterviewerList") val interviewerList : List<InterviewerList>,
	@SerializedName("EventType") val eventType : String,
	@SerializedName("APIResponse") val aPIResponse : String,
	@SerializedName("CandidateId") val candidateId : Int,
	@SerializedName("Password") val password : String,
	@SerializedName("subscriberid") val subscriberid : String,
	@SerializedName("selected") val selected : Boolean,
	@SerializedName("Duration") val duration : String,
	@SerializedName("interviewDuration") val interviewDuration : Int,
	@SerializedName("CandidateTimezone") val candidateTimezone : String,
	@SerializedName("type") val type : String,
	@SerializedName("jobid") val jobid : String,
	@SerializedName("RecruiterSearch") val recruiterSearch : String,
	@SerializedName("logedInUser") val logedInUser : String,
	@SerializedName("Job_ID_created_on") val job_ID_created_on : String,
	@SerializedName("Days_to_Hire") val days_to_Hire : Int,
	@SerializedName("JobDetailsFieldsList") val jobDetailsFieldsList : String,
	@SerializedName("SEQUENCE_ID") val sEQUENCE_ID : Int,
	@SerializedName("Interviewer_video_accesscode") val interviewer_video_accesscode : String,
	@SerializedName("Interviewer_Name") val interviewer_Name : String,
	@SerializedName("AssessmentExpiryDate") val assessmentExpiryDate : String,
	@SerializedName("MsteamToken") val msteamToken : String,
	@SerializedName("MeetingMode") val meetingMode : String,
	@SerializedName("MSMeetingMode") val mSMeetingMode : String,
	@SerializedName("MSMeetingUrl") val mSMeetingUrl : String,
	@SerializedName("MSTollNumber") val mSTollNumber : String,
	@SerializedName("MSDialingurl") val mSDialingurl : String,
	@SerializedName("VoiceCallPassCode") val voiceCallPassCode : String,
	@SerializedName("VoiceCallPhoneNumber") val voiceCallPhoneNumber : String
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


